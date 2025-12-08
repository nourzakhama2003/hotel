import { Component, Inject } from '@angular/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogRef, MatDialogModule, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UserProfile } from '../../models/userProfile';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FaceCaptureComponent } from '../face-capture/face-capture.component';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-profile',
  imports: [MatDialogModule, ReactiveFormsModule, MatSnackBarModule, CommonModule, FaceCaptureComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  profile!: Partial<UserProfile>;
  profileForm!: FormGroup;
  selectedImage!: string | ArrayBuffer | null;
  showFaceCapture = false;
  faceRegistered = false;

  constructor(
    private matDialogRef: MatDialogRef<ProfileComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private formBuilder: FormBuilder,
    private userService: UserService,
    private snackBar: MatSnackBar
  ) {
    this.profile = data;

    // Initialize faceRegistered from profile data or check backend
    this.faceRegistered = this.profile.faceAuthEnabled || false;

    // If not in profile, check if user has face auth enabled by fetching from backend
    if (!this.faceRegistered && this.profile.email) {
      this.checkFaceAuthStatus();
    }

    this.selectedImage = this.profile.profileImage || '/assets/images/userimage.png';

    this.profileForm = this.formBuilder.group({
      userName: [this.profile.userName],
      firstName: [this.profile.firstName],
      lastName: [this.profile.lastName],
      email: [this.profile.email],
      profileImage: [this.profile.profileImage]

    })

  }
  onSubmit() {

    const formValues = this.profileForm.value;
    this.profile = {
      ...this.profile,
      firstName: formValues.firstName,
      lastName: formValues.lastName,
      profileImage: this.selectedImage as string || formValues.profileImage
    };
    this.matDialogRef.close(this.profile);
  }
  onClose() {
    this.matDialogRef.close(null);
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

  openFaceCapture(): void {
    this.showFaceCapture = true;
  }

  onImageCaptured(blob: Blob): void {
    console.log('Image captured:', {
      size: blob.size,
      type: blob.type,
      email: this.profile.email,
      userName: this.profile.userName
    });

    if (!this.profile.email) {
      this.snackBar.open('Email not found. Please log in again.', 'Close', { duration: 5000 });
      this.showFaceCapture = false;
      return;
    }

    if (blob.size === 0) {
      this.snackBar.open('Captured image is empty. Please try again.', 'Close', { duration: 5000 });
      this.showFaceCapture = false;
      return;
    }

    const formData = new FormData();
    formData.append('faceImage', blob, 'face.jpg');
    formData.append('email', this.profile.email!);

    console.log('Sending face registration request...');

    this.userService.registerFace(formData).subscribe({
      next: (response) => {
        console.log('Face registration success:', response);
        this.snackBar.open('Face registered successfully!', 'Close', { duration: 3000 });
        this.faceRegistered = true;
        this.showFaceCapture = false;
      },
      error: (error) => {
        console.error('Face registration error:', error);
        console.error('Error status:', error.status);
        console.error('Error body:', error.error);
        const errorMsg = error.error?.message || error.message || 'Unknown error';
        this.snackBar.open('Failed to register face: ' + errorMsg, 'Close', { duration: 5000 });
        this.showFaceCapture = false;
      }
    });
  }

  onCaptureCancelled(): void {
    this.showFaceCapture = false;
  }

  private checkFaceAuthStatus(): void {
    // Fetch user profile from backend to check face auth status
    this.userService.getUserByEmail(this.profile.email!).subscribe({
      next: (response) => {
        if (response.user && response.user.faceAuthEnabled !== undefined) {
          this.faceRegistered = response.user.faceAuthEnabled;
          this.profile.faceAuthEnabled = response.user.faceAuthEnabled;
        }
      },
      error: (error) => {
        console.error('Error checking face auth status:', error);
      }
    });
  }
}
