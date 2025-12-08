import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastService } from '../../services/toast.service';
import { Room } from '../../models/room';
import { Hotel } from '../../models/hotel';
import { RoomSerice } from '../../services/room.service';
import { HotelService } from '../../services/hotel.service';
import { AppResponse } from '../../models/Response';
import { UserProfile } from '../../models/userProfile';
import { AppKeycloakService } from '../../keycloak/services/appKeycloakService';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BookingService } from '../../services/booking.service';
import { Booking } from '../../models/booking';

@Component({
  selector: 'app-room-details',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './room-details.component.html',
  styleUrl: './room-details.component.css'
})
export class RoomDetailsComponent implements OnInit {
  days!: number;
  preview = false;
  room!: Room;
  hotel!: Hotel;
  booking!: Partial<Booking>
  totalPrice!: number;
  minCheckIn: Date = new Date(new Date().getTime() + 10 * 60 * 1000);
  minCheckOut: Date = new Date(new Date().getTime() +24 * 60 * 60 * 1000)
  checkInDate: string = new Date(new Date().getTime() + 70 * 60 * 1000).toISOString().slice(0, 19);
  checkOutDate: string = new Date(new Date().getTime() + 25 * 60 * 60 * 1000 + 10 * 60 * 1000).toISOString().slice(0, 19);
  user!: Partial<UserProfile>;
  isLoading = false;
  constructor(
    private activatedRoute: ActivatedRoute,
    private toastService: ToastService,
    private roomService: RoomSerice,
    private hotelService: HotelService,
    private appKeycloaKService: AppKeycloakService,
    private bookingService: BookingService,
    private router: Router
  ) { }
  ngOnInit(): void {
    this.isLoading = true;
    this.activatedRoute.paramMap.subscribe((param) => {
      const id = Number(param.get("id"));
      if (!id) {
        this.isLoading = false;
        this.toastService.showAccesDenied("the id of the room is required");

      } else {
        this.roomService.getRoombyId(id).subscribe((response: AppResponse) => {
          this.room = response?.room!;
          this.isLoading = false;
          console.log('Room loaded:', this.room.id);

          // Load hotel data based on room's hotelId
          if (this.room.hotelId) {
            this.hotelService.getHotelbyId(this.room.hotelId).subscribe((hotelResponse: AppResponse) => {
              this.hotel = hotelResponse?.hotel!;
              console.log('Hotel loaded:', this.hotel);
            });
          }
        })
        this.appKeycloaKService.profileObservable.subscribe(profile => {
          this.user = profile!;
          console.log('User loaded:', this.user.id); // ✅ Now it's available
        })
      }
    })

  }

  showPreview() {
    this.isLoading = true;
    const checkIn = new Date(this.checkInDate);
    const checkOut = new Date(this.checkOutDate);
    console.log(checkIn);
    console.log(new Date(new Date().getTime() ));
    if (checkIn < new Date(new Date().getTime() )) {
      this.toastService.validationError('checkin date must be after now ');
      this.isLoading=false;
      return;
    }

    if (checkIn >= checkOut) {
      this.toastService.validationError('checkin date must be before checkout ');
      this.isLoading = false;
      return;
    }


    const diffrence = checkOut.getTime() - checkIn.getTime();
    const days = Math.round(diffrence / (24 * 60 * 60 * 1000));
    if (days < 1) {
      this.toastService.showError('booking days should be at least one day');
    }
    this.days = days;
    const totalPrice = (this.room.pricePerNight * this.days);
    if (!totalPrice) {
      this.toastService.showError('error while calculating totalprice');
    }
    this.totalPrice = totalPrice;
    this.isLoading = false;
    this.preview = true;

  }
  bookRoom() {

    const booking = {
      userId: this.user?.id,
      roomId: this.room?.id,
      checkInDate: this.checkInDate,
      checkOutDate: this.checkOutDate

    }
    // totalPrice: this.totalPrice
    console.log(this.room.id + " ," + this.user.id)
    console.log(booking);
    this.bookingService.addBooking(booking).subscribe({
      next: (response: AppResponse) => {
        this.booking = response.booking!
        this.toastService.showSuccess(`your booking is succefull , please procede the payment by cheking you email messages`, "SUCCESS");
        setTimeout(() => {
          this.router.navigate(['/rooms'])
        })
      },
      error: (err) => {
        this.toastService.showError(`${err.error.message}`, 'error while making the  booking');
      }
    })

  }
}
