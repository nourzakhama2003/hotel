import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingService } from '../../services/booking.service';
import { ToastService } from '../../services/toast.service';
import { Booking } from '../../models/booking';
import { AppResponse } from '../../models/Response';
import { BookingCardComponent } from '../../components/booking/booking-card/booking-card.component';

@Component({
  selector: 'app-bookings',
  standalone: true,
  imports: [CommonModule, FormsModule, BookingCardComponent],
  templateUrl: './bookings.component.html',
  styleUrl: './bookings.component.css'
})
export class BookingsComponent {
  searchReference: string = '';
  booking: Booking | null = null;
  isLoading: boolean = false;
  hasSearched: boolean = false;

  constructor(
    private bookingService: BookingService,
    private toastService: ToastService
  ) { }

  searchBooking() {
    if (!this.searchReference.trim()) {
      this.toastService.validationError('Please enter a booking reference');
      return;
    }

    this.isLoading = true;
    this.hasSearched = true;
    this.booking = null;

    this.bookingService.getBookingByReference(this.searchReference.trim()).subscribe({
      next: (response: AppResponse) => {
        if (response.booking) {
          this.booking = response.booking;
          this.toastService.showSuccess('Booking found successfully');
        } else {
          this.booking = null;
          this.toastService.showError('No booking found with this reference');
        }
        this.isLoading = false;
      },
      error: (err) => {
        this.booking = null;
        this.isLoading = false;
        this.toastService.showError(
          err.error?.message || 'Booking not found',
          'Search Error'
        );
      }
    });
  }

  clearSearch() {
    this.searchReference = '';
    this.booking = null;
    this.hasSearched = false;
  }

  onEnterKey(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.searchBooking();
    }
  }
}
