import { Component, Input, OnDestroy, OnInit, Output, EventEmitter, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { Room } from '../../../models/room';
import { Subscription } from 'rxjs';
import { AppKeycloakService } from '../../../keycloak/services/appKeycloakService';
import { HotelService } from '../../../services/hotel.service';
import { AppResponse } from '../../../models/Response';
import { Hotel } from '../../../models/hotel';
import { ToastService } from '../../../services/toast.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-room-card',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './room-card.component.html',
  styleUrl: './room-card.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RoomcardComponent implements OnInit, OnDestroy {
  @Input() room!: Room;
  @Input() allRooms = true;

  hotel?: Hotel; // Made optional since it loads asynchronously
  isLoading = false;
  admin = false;
  profileSubscription!: Subscription;
  @Output() onUpdate = new EventEmitter<Room>();
  @Output() onDelete = new EventEmitter<number>();
  constructor(
    private appkeycloakService: AppKeycloakService,
    private hotelService: HotelService,
    private toastService: ToastService,
    private cdr: ChangeDetectorRef
  ) { }
  ngOnInit(): void {
    console.log('Room card initialized with room:', this.room); // Debug log

    this.profileSubscription = this.appkeycloakService.profileObservable.subscribe(profile => {
      this.admin = profile?.role && profile?.role === 'Admin' ? true : false;
    });

    // Only fetch hotel if we have a valid hotelId and haven't already fetched it
    if (this.room.hotelId && !this.hotel && !this.isLoading) {
      console.log('Fetching hotel with ID:', this.room.hotelId); // Debug log
      this.gethotelById(this.room.hotelId);
    }
  }
  ngOnDestroy(): void {
    if (this.profileSubscription) {
      this.profileSubscription.unsubscribe();
    }

  }


  update(room: Room) {
    this.onUpdate.emit(room);

  }
  delete(id: number) {
    this.onDelete.emit(id);
  }
  gethotelById(id: number) {
    if (this.isLoading || this.hotel) return; // Prevent multiple calls

    this.isLoading = true;
    this.hotelService.getHotelbyId(id).subscribe({
      next: (response: AppResponse) => {
        if (response?.hotel) {
          this.hotel = response.hotel;
          console.log('Hotel loaded:', this.hotel); // Debug log
        }
        this.isLoading = false;
        this.cdr.detectChanges(); // Trigger change detection
      },
      error: (err) => {
        console.error('Error fetching hotel:', err);
        this.toastService.showError(`${err.error?.message || 'Failed to load hotel details'}`, 'ERROR while getting hotel of the room');
        this.isLoading = false;
        this.cdr.detectChanges(); // Trigger change detection even on error
      }
    });
  }
}
