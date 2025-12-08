import { Component, OnInit } from '@angular/core';
import { RoomListComponent } from '../../components/room/room-list/room-list.component';
import { CommonModule } from '@angular/common';
import { SpinnerComponent } from '../../components/constants/spinner/spinner.component';
import { Room } from '../../models/room';
import { Hotel } from '../../models/hotel';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ToastService } from '../../services/toast.service';
import { HotelService } from '../../services/hotel.service';
import { MatDialog } from '@angular/material/dialog';
import { RoomSerice } from '../../services/room.service';
import { AppResponse } from '../../models/Response';
import { RoomFomComponent } from '../../components/room/room-fom/room-fom.component';
import { RoomSearchComponent } from '../../components/room/room-search/room-search.component';
import { ConfirmDialogComponent } from '../../components/confirmDialog/confirm-dialog.component';

@Component({
  selector: 'app-all-rooms',
  standalone: true,
  imports: [RoomListComponent, CommonModule, SpinnerComponent, RoomSearchComponent, RouterLink],
  templateUrl: './all-rooms.component.html',
  styleUrl: './all-rooms.component.css'
})
export class AllRoomsComponent implements OnInit {

  hotelId!: number;
  isLoading = false;
  rooms: Room[] = [];
  hotel: Hotel | undefined;
  constructor(private activatedRoute: ActivatedRoute, private toastService: ToastService, private hotelService: HotelService, private matDialog: MatDialog, private roomService: RoomSerice) {

  }

  ngOnInit(): void {
    this.loadRooms();
  }


  loadRooms() {
    this.isLoading = true;
    this.roomService.getRooms().subscribe({
      next: (response: AppResponse) => {
        this.rooms = response.rooms! || [];
        this.isLoading = false;


      },
      error: (err) => {
        this.toastService.showError(`${err.error.message}`, 'ERROR');
        this.isLoading = false;

      }
    })
  }
  addRoom(room?: Room) {
    const formRef = this.matDialog.open(RoomFomComponent, {
      width: '50%',
      height: '80vh',
      data: room || { hotelId: this.hotelId }
    })



    formRef.afterClosed().subscribe({

      next: (roomData: Room) => {
        if (roomData) {
          this.isLoading = true;
          if (room) {
            this.roomService.updateRoom(room.id, roomData).subscribe({
              next: (response: AppResponse) => {

                const roomIndex = this.rooms.findIndex(rm => rm.id === room.id);
                this.rooms[roomIndex] = response?.room!;
                this.rooms = [...this.rooms];
                this.isLoading = false;
                this.toastService.showSuccess(`${response.message}`, "Success");

              },
              error: (err) => {
                this.isLoading = false;
                this.toastService.showError(`${err.error.message}`, "Error while creating room");
              }
            })



          } else {
            this.roomService.addRoom(roomData).subscribe({
              next: (response: AppResponse) => {
                this.rooms = [...this.rooms || [], response?.room!];
                this.isLoading = false;
                this.toastService.showSuccess(`${response.message}`, "Success");

              },
              error: (err) => {
                this.isLoading = false;
                this.toastService.showError(`${err.error.message}`, "Error while creating room");
              }
            })

          }
        }

      },
      error: (err) => {
        this.isLoading = false;
        this.toastService.showError(`${err.error.message},"ERROR While creating room`);
      }
    })
  }
  searchAvailableRooms(rooms: Room[]) {
    this.rooms = [...rooms];

  }

  update(room: Room) {
    this.addRoom(room);

  }
  delete(id: number) {
    const matDialogRef = this.matDialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Confirmer la suppression',
        message: 'Êtes-vous sûr de vouloir supprimer ce hotel ? Cette action est irréversible.',
        confirmText: 'Supprimer',
        cancelText: 'Annuler'
      }
    })


    matDialogRef.afterClosed().subscribe({
      next: (result) => {
        if (result) {
          this.roomService.deleteRoom(id).subscribe({
            next: (response: AppResponse) => {
              this.rooms = this.rooms.filter(rm => rm.id != id);
              this.isLoading = false;
              this.toastService.showSuccess(`${response.message}`, "Success");

            },
            error: (err) => {
              this.isLoading = false;
              this.toastService.showError(`${err.error.message}`, "Error while deleting room");
            }
          })
        }
      },
      error: () => {
        this.toastService.showError("ERROR while closing the dialog confirmation", "ERROR");
      }
    })

  }

  getAvailableRoomsCount(): number {
    return this.rooms.length; // For now, assume all rooms are available
    // You can add more complex logic here to check actual availability
  }

  getPremiumRoomsCount(): number {
    // Count rooms that are considered premium (e.g., SWIT type or price > 200)
    return this.rooms.filter(room =>
      room.type === 'SWIT' || room.pricePerNight > 200
    ).length;
  }
}
