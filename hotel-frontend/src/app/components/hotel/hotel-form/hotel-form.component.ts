import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogModule, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { Hotel } from '../../../models/hotel';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../services/toast.service';
import { SpinnerComponent } from '../../constants/spinner/spinner.component';

@Component({
  selector: 'app-hotel-form',
  imports: [ReactiveFormsModule, MatDialogModule, CommonModule, SpinnerComponent, MatDialogTitle, MatDialogActions, MatDialogContent],
  templateUrl: './hotel-form.component.html',
  styleUrl: './hotel-form.component.css'
})
export class HotelFormComponent {
  hotelForm!: FormGroup;
  isEditing = false;
  isLoading = false;
  hotelData: Hotel;
  selectedImage: string | undefined;

  constructor(@Inject(MAT_DIALOG_DATA) data: Hotel, private formBuilder: FormBuilder, private matDialogRef: MatDialogRef<HotelFormComponent>, private toastService: ToastService) {
    this.hotelData = data; // Store the original hotel data
    this.isEditing = !!data?.hotelName;
    this.selectedImage = data?.hotelImage;
    this.hotelForm = this.formBuilder.group({
      hotelName: [data?.hotelName || '', Validators.required],
      hotelLocation: [data?.hotelLocation || '', Validators.required],
      hotelImage: [this.selectedImage]
    });
  }
  close() {
    this.matDialogRef.close();
  }

  onSubmit() {
    this.isLoading = true;
    if (this.hotelForm.valid) {
      // Prepare the hotel data to return
      const formData = this.hotelForm.value;

      // If editing, include the original ID
      const hotelDataToReturn = this.isEditing
        ? {
          ...formData, id: this.hotelData.id

        }
        : formData;

      this.matDialogRef.close(hotelDataToReturn);
    }
    else {
      // Reset loading state when form is invalid
      this.isLoading = false;

      // Mark all fields as touched to show validation errors
      this.hotelForm.markAllAsTouched();
      const invalidFields = Object.keys(this.hotelForm.controls).filter(field => this.hotelForm.get(field)?.invalid);

      // Show validation error with our custom service
      this.toastService.validationError(" " + invalidFields.join(" , "));

      console.log('Toast calls completed');
    }
  }


  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.selectedImage = reader.result as string;
        // Update form control after FileReader completes
        this.hotelForm.patchValue({ hotelImage: this.selectedImage });
      };
      reader.readAsDataURL(file);
    }
  }

  removeImage() {
    this.selectedImage = undefined;
    this.hotelForm.patchValue({ hotelImage: null });
  }

}