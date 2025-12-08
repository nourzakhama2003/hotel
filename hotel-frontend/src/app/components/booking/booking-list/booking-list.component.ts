import { Component, Input } from '@angular/core';
import { Booking } from '../../../models/booking';
import { BookingCardComponent } from '../booking-card/booking-card.component';

@Component({
  selector: 'app-booking-list',
  imports: [BookingCardComponent],
  templateUrl: './booking-list.component.html',
  styleUrl: './booking-list.component.css'
})
export class BookingListComponent {
@Input() bookings!:Booking[];
}
