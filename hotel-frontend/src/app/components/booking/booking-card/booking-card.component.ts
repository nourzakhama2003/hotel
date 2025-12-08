import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Booking } from '../../../models/booking';
import { Booknigstatus } from '../../../models/enums/bookingStatus';
import { PaymentStatus } from '../../../models/enums/paymentStatus';
import { ToastService } from '../../../services/toast.service';

@Component({
  selector: 'app-booking-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './booking-card.component.html',
  styleUrl: './booking-card.component.css'
})
export class BookingCardComponent {
  @Input() booking!: Booking;
constructor(private toastService:ToastService){}
  get checkInDate(): Date {
    return new Date(this.booking.checkInDate);
  }

  get checkOutDate(): Date {
    return new Date(this.booking.checkOutDate);
  }

  get nightsCount(): number {
    const diffTime = this.checkOutDate.getTime() - this.checkInDate.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }

  get isUpcoming(): boolean {
    return this.checkInDate > new Date();
  }

  get isActive(): boolean {
    const now = new Date();
    return this.checkInDate <= now && this.checkOutDate >= now;
  }

  get isPast(): boolean {
    return this.checkOutDate < new Date();
  }

  get statusClass(): string {
    if (this.booking.bookingStatus === Booknigstatus.CANCELLED) return 'status-cancelled';
    if (this.booking.bookingStatus === Booknigstatus.CHECKED_IN) return 'status-checked-in';
    if (this.booking.bookingStatus === Booknigstatus.CHECKED_OUT) return 'status-checked-out';
    if (this.booking.bookingStatus === Booknigstatus.BOOKED) return 'status-booked';
    return 'status-pending';
  }

  get paymentStatusClass(): string {
    if (this.booking.paymentStatus === PaymentStatus.COMPLETED) return 'payment-completed';
    if (this.booking.paymentStatus === PaymentStatus.FAILED) return 'payment-failed';
    return 'payment-pending';
  }
  checkEmail(){
    this.toastService.showInfo(" check your email to procede ");
  }
}
