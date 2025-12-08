import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { loadStripe, Stripe, StripeElements, StripeCardElement, PaymentIntent } from '@stripe/stripe-js';
import { ToastService } from '../../services/toast.service';
import { environment } from '../../../environments/environment';
import { PaymentService } from '../../services/payment.service';
import { AppResponse } from '../../models/Response';
import { BookingService } from '../../services/booking.service';
import { PaymentStatus } from '../../models/enums/paymentStatus';
import { PaymentGateway } from '../../models/enums/paymentgateway';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-payment',
  imports: [CommonModule],
  templateUrl: './payment.component.html',
  styleUrl: './payment.component.css'
})
export class PaymentComponent implements OnInit {
  bookingReference!: string;
  error!: string;
  amount!: number;
  stripe: Stripe | null = null;
  stripeElements: StripeElements | null = null;
  stripeCardElement: StripeCardElement | null = null;
  clientSecrete: any = null;
  isLoading = false;
  private initialized = false;

  constructor(private bookingService: BookingService, private paymentService: PaymentService, private router: Router, private activatedRoute: ActivatedRoute, private toastService: ToastService) {
  }
  async ngOnInit() {
    if (this.initialized) {
      console.log("Component already initialized, skipping");
      return;
    }
    this.initialized = true;
    this.activatedRoute.paramMap.subscribe(params => {
      this.bookingReference = params.get('bookingRefrence')!;
      this.amount = Number(params.get('amount'));
      console.log("Payment params - Booking Reference:", this.bookingReference, "Amount:", this.amount);

      if (!this.bookingReference || !this.amount) {
        this.toastService.showError("Invalid payment parameters");
        this.router.navigate(['/']);
        return;
      }
    });

    try {
      this.stripe = await loadStripe(environment.STRIPE_PUBLIC_KEY);
      console.log("Stripe loaded:", !!this.stripe);

      if (this.stripe) {
        this.stripeElements = this.stripe.elements({
          fonts: [
            { cssSrc: 'https://fonts.googleapis.com/css2?family=Inter:wght@400;500&display=swap' }
          ]
        });

        this.stripeCardElement = this.stripeElements.create('card', {
          style: {
            base: {
              color: '#000',
              fontFamily: '"Inter", sans-serif',
              fontSize: '16px',
              '::placeholder': {
                color: '#aab7c4'
              },
            },
            invalid: {
              color: '#fa755a',
              iconColor: '#fa755a'
            }
          }
        });

        this.stripeCardElement.mount('#card-element');

        this.stripeCardElement.on('change', (event) => {
          const displayError = document.getElementById('card-errors');
          if (displayError) {
            if (event.error) {
              displayError.textContent = event.error.message;
            } else {
              displayError.textContent = '';
            }
          }
        });

        this.stripeCardElement.on('focus', () => {
          const container = document.getElementById('stripe-card-container');
          if (container) {
            container.classList.add('focused');
          }
        });

        this.stripeCardElement.on('blur', () => {
          const container = document.getElementById('stripe-card-container');
          if (container) {
            container.classList.remove('focused');
          }
        });

        this.fetchClientId();
      } else {
        this.toastService.showError("Failed to load Stripe");
      }
    } catch (error) {
      console.error("Error loading Stripe:", error);
      this.toastService.showError("Failed to initialize payment system");
    }
  }
  fetchClientId() {
    // Prevent multiple calls to avoid creating multiple payment intents
    if (this.clientSecrete) {
      console.log("Client secret already exists, skipping fetch");
      return;
    }

    const paymentData = {
      bookingReference: this.bookingReference,
      amount: this.amount
    }
    console.log("Sending payment data:", paymentData);
    this.paymentService.initialisePayment(paymentData).subscribe({
      next: (response: AppResponse) => {
        this.clientSecrete = response.transactionId;
        console.log("Client secret received: " + this.clientSecrete);
        if (!this.clientSecrete) {
          this.toastService.showError("Failed to get payment client secret");
        }
      },
      error: (err) => {
        console.error("Error fetching client secret:", err);
        this.toastService.showError(`${err.error?.message || 'Failed to initialize payment'}`);
        this.showError(
          err?.error?.message || 'failed to fetch transaction unique secret'
        );
      }
    })
  }
  showError(msg: any): void {
    this.error = msg;
    setTimeout(() => {
      this.error = '';
    }, 5000);
  }

  onPayButtonClick(event: Event) {
    console.log("=== PAY BUTTON CLICKED ===");
    console.log("Button click event:", event);
    console.log("Button disabled state:", this.isLoading || !this.stripe);
  }

  async handleSubmit(event?: Event) {
    // Prevent form submission if it's a form event
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }

    console.log("=== PAYMENT SUBMISSION STARTED ===");
    console.log("Button clicked or form submitted");
    console.log("Stripe instance:", !!this.stripe);
    console.log("Stripe elements:", !!this.stripeElements);
    console.log("Card element:", !!this.stripeCardElement);
    console.log("Client secret:", !!this.clientSecrete);
    console.log("Is loading:", this.isLoading);

    if (this.isLoading) {
      console.log("Payment already in progress, ignoring click");
      return;
    }

    if (!this.stripe || !this.stripeElements || !this.stripeCardElement) {
      console.error("Stripe not properly initialized");
      this.toastService.showError("Payment system not ready. Please refresh the page.");
      return;
    }

    if (!this.clientSecrete) {
      console.error("No client secret available");
      this.toastService.showError("Payment not initialized. Please refresh and try again.");
      return;
    }

    console.log("=== VALIDATION PASSED - PROCEEDING WITH PAYMENT ===");

    this.isLoading = true;
    console.log("Starting payment confirmation with client secret:", this.clientSecrete);

    try {
      console.log("About to call confirmCardPayment...");
      const result = await this.stripe.confirmCardPayment(
        this.clientSecrete,
        {
          payment_method: {
            card: this.stripeCardElement,
            billing_details: {
              name: 'Test Customer',
            }
          }
        }
      );

      console.log("=== STRIPE RESPONSE RECEIVED ===");
      console.log("Full result:", result);
      const { error, paymentIntent } = result;

      console.log("=== STRIPE RESPONSE ===");
      console.log("Error:", error);
      console.log("PaymentIntent:", paymentIntent);
      console.log("PaymentIntent Status:", paymentIntent?.status);

      if (error) {
        console.error("=== PAYMENT FAILED ===");
        console.error("Error details:", error);
        this.isLoading = false;

        this.toastService.showError(`Payment failed: ${error.message}`);

        // Record failed payment
        const failedPaymentData = {
          bookingReference: this.bookingReference,
          transactionId: 'failed_' + Date.now(),
          amount: this.amount,
          success: false,
          failueReason: error.message || 'Payment failed',
          paymentGateway: PaymentGateway.STRIPE,
          paymentDate: new Date().toISOString()
        };

        this.paymentService.createPayment(failedPaymentData).subscribe({
          next: (response) => console.log('Failed payment recorded:', response),
          error: (err) => console.error('Error recording failed payment:', err)
        });

        return;
      }

      // Check for all possible successful states
      if (paymentIntent && (paymentIntent.status === "succeeded" || paymentIntent.status === "processing")) {
        console.log("=== PAYMENT SUCCEEDED ===");
        console.log("PaymentIntent ID:", paymentIntent.id);
        console.log("PaymentIntent status:", paymentIntent.status);

        this.isLoading = false;

        // Create payment record in database
        const paymentData = {
          bookingReference: this.bookingReference,
          transactionId: paymentIntent.id,
          amount: this.amount,
          success: true,
          paymentGateway: PaymentGateway.STRIPE,
          paymentDate: new Date().toISOString()
        };

        console.log("Creating payment record with data:", paymentData);

        this.paymentService.createPayment(paymentData).subscribe({
          next: (paymentResponse: any) => {
            console.log('=== PAYMENT RECORD CREATED ===');
            console.log('Response:', paymentResponse);

            // Update booking status
            this.bookingService.updatebookingByRefrence(this.bookingReference, {
              paymentStatus: PaymentStatus.COMPLETED
            }).subscribe({
              next: (bookingResponse: AppResponse) => {
                console.log('=== BOOKING UPDATED ===');
                console.log('Response:', bookingResponse);
                this.toastService.showSuccess("Payment completed successfully!");
                this.router.navigate(['/payment-success', this.bookingReference]);
              },
              error: (err) => {
                console.error('=== BOOKING UPDATE FAILED ===');
                console.error('Error:', err);
                this.toastService.showError("Payment successful but failed to update booking");
                this.router.navigate(['/payment-success', this.bookingReference]);
              }
            });
          },
          error: (err) => {
            console.error('=== PAYMENT RECORD CREATION FAILED ===');
            console.error('Error:', err);
            this.toastService.showError("Payment successful but failed to save record");
            this.router.navigate(['/payment-success', this.bookingReference]);
          }
        });
      } else {
        console.error("=== PAYMENT INCOMPLETE ===");
        console.error("PaymentIntent status:", paymentIntent?.status);
        console.error("PaymentIntent:", paymentIntent);
        this.isLoading = false;

        // Handle different incomplete states
        if (paymentIntent?.status === "requires_payment_method") {
          this.toastService.showError('Your card was declined. Please try a different payment method.');
        } else if (paymentIntent?.status === "requires_confirmation") {
          this.toastService.showError('Payment requires additional confirmation. Please try again.');
        } else if (paymentIntent?.status === "requires_action") {
          this.toastService.showError('Payment requires additional authentication. Please complete the verification.');
        } else {
          this.toastService.showError(`Payment incomplete. Status: ${paymentIntent?.status || 'Unknown'}`);
        }
      }

    } catch (stripeError) {
      console.error("=== STRIPE ERROR ===");
      console.error("Stripe error:", stripeError);
      this.isLoading = false;
      this.toastService.showError('Payment system error. Please try again.');
    }
  }

  // Helper method to update card element visual state
  private updateCardElementClass(state: string): void {
    const cardElement = document.getElementById('card-element');
    if (cardElement) {
      // Remove all state classes
      cardElement.classList.remove('card-ready', 'card-focus', 'card-blur', 'card-complete', 'card-error', 'card-default');

      // Add the current state class
      switch (state) {
        case 'ready':
          cardElement.classList.add('card-ready');
          break;
        case 'focus':
          cardElement.classList.add('card-focus');
          break;
        case 'blur':
          cardElement.classList.add('card-blur');
          break;
        case 'complete':
          cardElement.classList.add('card-complete');
          break;
        case 'error':
          cardElement.classList.add('card-error');
          break;
        default:
          cardElement.classList.add('card-default');
          break;
      }
    }
  }

  overrideStripeStyles() {
    // This method is no longer needed as we are relying on the Stripe Element's 'style' object.
  }
}
