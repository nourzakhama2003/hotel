import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../../services/toast.service';
import { Roomtype } from '../../../models/enums/roomType';
import { CommonModule } from '@angular/common';
import { RoomSerice } from '../../../services/room.service';
import { AppResponse } from '../../../models/Response';
import { Room } from '../../../models/room';
import { json } from 'stream/consumers';



@Component({
  selector: 'app-room-search',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './room-search.component.html',
  styleUrl: './room-search.component.css'
})
export class RoomSearchComponent {
  constructor(private toastService: ToastService, private roomService: RoomSerice) { }

  @Output() onSearch = new EventEmitter<Room[]>();
  isLoading = false;
  roomType = [Roomtype.SINGLE, Roomtype.DOUBLE, Roomtype.TRIPLE, Roomtype.SWIT];
  selectedType: string = '';
  rooms: Room[] = [];
  // Initialize with current date + 1 hour for check-in
  minCheckin = new Date(
    new Date().getTime() + 60 * 60 * 1000 // add 1 hour
  ).toISOString().slice(0, 19);
  minCheckout = new Date(
    new Date().getTime() + 25 * 60 * 60 * 1000 // add 25 hours
  ).toISOString().slice(0, 19);
  checkInDate: string = new Date(
    new Date().getTime() + 70 * 60 * 1000 // add 1 hour
  ).toISOString().slice(0, 19); // Format: YYYY-MM-DDTHH:mm

  // Initialize with current date + 25 hours for check-out
  checkOutDate: string = new Date(
    new Date().getTime() + 25 * 60 * 60 * 1000+10*60*1000 // add 25 hours
  ).toISOString().slice(0, 19); // Format: YYYY-MM-DDTHH:mm







  searchAvailableRooms() {
    this.isLoading = true;
    const checkIn = new Date(this.checkInDate);
    const checkOut = new Date(this.checkOutDate);

    if (checkIn >= checkOut) {
      this.toastService.validationError("Checkout date must be after the checkin date");
      this.isLoading = false;
      return;
    }
    if (this.selectedType) {
      console.log("selectedtype" + this.selectedType);
    }

    console.log("chekin" + this.checkInDate);
    this.roomService.getAvailableRooms(this.checkInDate, this.checkOutDate, this.selectedType).subscribe({
      next: (response: AppResponse) => {
        this.rooms = response?.rooms!;
        this.onSearch.emit(this.rooms);
        this.isLoading = false;

      },
      error: (err) => {
        this.toastService.showError(`${err.error.message}`, "Error while getting  available rooms");

      }
    })
    // Here you can call your search service with the proper ISO dates
    // Example: this.roomService.searchAvailableRooms(checkInISO, checkOutISO);
  }
}
