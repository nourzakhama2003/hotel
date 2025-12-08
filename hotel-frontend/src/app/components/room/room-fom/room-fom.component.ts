import { CommonModule } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogModule,
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions
} from '@angular/material/dialog';
import { Room } from '../../../models/room';
import { Roomtype } from '../../../models/enums/roomType';
import { HotelService } from '../../../services/hotel.service';
import { Hotel } from '../../../models/hotel';
import { AppResponse } from '../../../models/Response';
import { ToastService } from '../../../services/toast.service';
import { ActivatedRoute } from '@angular/router';
import { RoomSerice } from '../../../services/room.service';

@Component({
  selector: 'app-room-fom',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions
  ],
  templateUrl: './room-fom.component.html',
  styleUrl: './room-fom.component.css'
})
export class RoomFomComponent implements OnInit {
  isEditing!: boolean;
  isLoading = false;
  message = '';
  roomForm!: FormGroup
  roomTypes = [Roomtype.SINGLE, Roomtype.DOUBLE, Roomtype.SWIT];
  hotels!: Hotel[];
  hotel!: Hotel;
  selectedImage: string | undefined;
  currentRoomNumber!: number;

  constructor(
    private formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: Room | null,
    private matDialogRef: MatDialogRef<RoomFomComponent>,
    private hotelService: HotelService,
    private toastservice: ToastService,
    private roomService: RoomSerice
  ) {
    const id = data?.hotelId;
    this.isEditing = !!(data && data.id);
    this.selectedImage = data?.roomImage;

    // Create form immediately to prevent template errors
    this.createForm();

    // Then get room number for new rooms
    if (!this.isEditing) {
      this.getRoomNumber();
    }
  }
  ngOnInit(): void {
    this.loadHotels();
  }

  createForm() {
    this.roomForm = this.formBuilder.group({
      roomNumber: [this.data?.roomNumber || this.currentRoomNumber || 1, [Validators.required, Validators.min(1)]],
      capacity: [this.data?.capacity || 1, [Validators.required, Validators.min(1)]],
      type: [this.data?.type || '', [Validators.required]],
      pricePerNight: [this.data?.pricePerNight || 1, [Validators.required, Validators.min(1)]],
      hotelId: [{ value: this.data?.hotelId || '', disabled: false }, [Validators.required, Validators.min(1)]],
      description: [this.data?.description || ""],
      roomImage: [this.selectedImage]
    });
  }

  getRoomNumber() {
    this.isLoading = true;
    this.roomService.getRoomNumber().subscribe({
      next: (response: AppResponse) => {
        console.log(response);
        this.currentRoomNumber = response?.roomNumber!;
        console.log("current room number", this.currentRoomNumber);
        // Update the existing form with the correct room number
        this.roomForm.patchValue({
          roomNumber: this.currentRoomNumber
        });
        this.isLoading = false;
      },
      error: (err) => {
        this.message = err.error.message;
        this.toastservice.showError(`${this.message}`);
        this.isLoading = false;
        // Keep default room number (1) if fetch fails
      }
    })
  }
  loadHotels() {
    this.isLoading = true
    this.hotelService.getHotels().subscribe(
      {
        next: (response: AppResponse) => {
          this.hotels = response.hotels || [];
          this.isLoading = false;
        },
        error: (err) => {
          this.message = err.error.message;
          this.toastservice.showError(`${this.message}`);
          this.isLoading = false;

        }
      }
    )
  }

  onSubmit() {
    this.isLoading = true;
    console.log(this.roomForm.value);

    if (this.roomForm.invalid) {
      this.roomForm.markAllAsTouched()
      const invalidFields = Object.keys(this.roomForm.controls).filter(field => this.roomForm.get(field)?.invalid)
      this.toastservice.validationError(" " + invalidFields.join(' , '));
      this.isLoading = false;

    } else {
      this.matDialogRef.close(this.roomForm.value);
      this.isLoading = false;
    }

  }

  onClose() {
    this.matDialogRef.close();
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {

      const reader = new FileReader();
      reader.onload = () => {
        this.selectedImage = reader.result as string;
        // Update form control after FileReader completes
        this.roomForm.patchValue({ roomImage: this.selectedImage });
      };
      reader.readAsDataURL(file);
    }
  }

  removeImage(event: Event) {
    event.preventDefault();
    event.stopPropagation();
    this.selectedImage = undefined;
    this.roomForm.patchValue({ roomImage: null });
  }

}
