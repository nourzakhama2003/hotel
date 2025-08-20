import { Component, Inject } from '@angular/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogRef, MatDialogModule, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UserProfile } from '../../keycloak/userProfile';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile',
  imports: [MatDialogModule, ReactiveFormsModule, MatSnackBarModule, CommonModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  profile!: Partial<UserProfile>;
  profileForm!: FormGroup;
  selectedImage!: string | ArrayBuffer | null;
  constructor(private matDialogRef: MatDialogRef<ProfileComponent>, @Inject(MAT_DIALOG_DATA) public data: any, private matSnackBarr: MatSnackBar, private formBuilder: FormBuilder) {
    this.profile = data;

    console.log('Profile data received:', this.profile); // Debug log

    // Initialize selectedImage with existing profile image if available
    this.selectedImage = this.profile.profileImage || null;

    this.profileForm = this.formBuilder.group({
      userName: [this.profile.userName],
      firstName: [this.profile.firstName],
      lastName: [this.profile.lastName],
      email: [this.profile.email],
      profileImage: [this.profile.profileImage]

    })

  }
  onSubmit() {
    // Update profile with form values before closing
    const formValues = this.profileForm.value;

    // Debug logs to check what we're sending
    console.log('Form values:', formValues);
    console.log('Selected image:', this.selectedImage);
    console.log('Profile image from form:', formValues.profileImage);

    this.profile = {
      ...this.profile,
      firstName: formValues.firstName,
      lastName: formValues.lastName,
      // Use the selectedImage if available, otherwise use form value
      profileImage: this.selectedImage as string || formValues.profileImage
    };

    console.log('Final profile to send:', this.profile);
    this.matDialogRef.close(this.profile);
  }
  onClose() {
    this.matDialogRef.close();
  }
  onFileSelected(event: any) {
    const file = event.target.files[0];

    if (file) {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        this.selectedImage = reader.result;
        this.profileForm.patchValue({ profileImage: this.selectedImage });
        this.profile.profileImage = this.selectedImage as string;
      }
    }
  }
}
