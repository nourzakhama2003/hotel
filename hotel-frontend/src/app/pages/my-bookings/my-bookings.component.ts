import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Booking } from '../../models/booking';
import { BookingService } from '../../services/booking.service';
import { UserProfile } from '../../models/userProfile';
import { AppKeycloakService } from '../../keycloak/services/appKeycloakService';
import { AppResponse } from '../../models/Response';
import { ToastService } from '../../services/toast.service';
import { BookingCardComponent } from '../../components/booking/booking-card/booking-card.component';

@Component({
  selector: 'app-my-bookings',
  standalone: true,
  imports: [CommonModule, RouterModule, BookingCardComponent],
  templateUrl: './my-bookings.component.html',
  styleUrl: './my-bookings.component.css'
})
export class MyBookingsComponent implements OnInit {
  bookings: Booking[] = [];
  user!: Partial<UserProfile>;
  isLoading = true;

  constructor(
    private toastService: ToastService,
    private bookingService: BookingService,
    private appKeycloakService: AppKeycloakService
  ) { }

  ngOnInit(): void {
    this.appKeycloakService.profileObservable.subscribe((profile) => {
      this.user = profile!;
      if (this.user?.id) {
        this.loadBookings();
      }
    });
  }

  loadBookings() {
    this.isLoading = true;
    this.bookingService.getBookingbyUserId(this.user?.id!).subscribe({
      next: (response: AppResponse) => {
        this.bookings = response.bookings! || [];
        this.isLoading = false;
      },
      error: (err) => {
        this.toastService.showError(`${err.error.message}`, 'Error while getting your bookings');
        this.isLoading = false;
      }
    });
  }

  get upcomingBookings(): Booking[] {
    return this.bookings.filter(booking => new Date(booking.checkInDate) > new Date());
  }

  get activeBookings(): Booking[] {
    const now = new Date();
    return this.bookings.filter(booking =>
      new Date(booking.checkInDate) <= now && new Date(booking.checkOutDate) >= now
    );
  }

  get pastBookings(): Booking[] {
    return this.bookings.filter(booking => new Date(booking.checkOutDate) < new Date());
  }
}
