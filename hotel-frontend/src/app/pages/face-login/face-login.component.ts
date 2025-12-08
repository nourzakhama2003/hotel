import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FaceCaptureComponent } from '../../components/face-capture/face-capture.component';
import { FaceAuthService, FaceAuthResponse } from '../../services/face-auth.service';
import { AppKeycloakService } from '../../keycloak/services/appKeycloakService';
import { UserService } from '../../services/user.service';
import { UserProfile } from '../../models/userProfile';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-face-login',
  standalone: true,
  imports: [CommonModule, FaceCaptureComponent],
  templateUrl: './face-login.component.html',
  styleUrl: './face-login.component.css'
})
export class FaceLoginComponent implements OnInit {
  showCamera = false;
  isProcessing = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private faceAuthService: FaceAuthService,
    private keycloakService: AppKeycloakService,
    private userService: UserService,
    private router: Router,
    private toastService:ToastService
  ) { }

  ngOnInit(): void {
    // Check if user is already logged in
    if (this.keycloakService.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  startFaceLogin(): void {
    this.showCamera = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onImageCaptured(imageBlob: Blob): void {
    this.isProcessing = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.faceAuthService.authenticateByFace(imageBlob).subscribe({
      next: (response: FaceAuthResponse) => {
        this.isProcessing = false;
        this.showCamera = false;

        if (response.success && response.userName) {
          // Check if we received an authentication token
          if (response.access_token) {
            this.successMessage = `Welcome back, ${response.userName}! Logging you in...`;

            // Store the authentication token
            localStorage.setItem('access_token', response.access_token);
            localStorage.setItem('token_type', response.token_type || 'Bearer');

            if (response.refresh_token) {
              localStorage.setItem('refresh_token', response.refresh_token);
            }

            // Fetch complete user profile from backend
            if (response.email) {
              this.userService.getUserByEmail(response.email).subscribe({
                next: (profileResponse) => {
                  if (profileResponse.user) {
                    const userProfile: UserProfile = profileResponse.user;

                    // Store complete user info for profile display
                    localStorage.setItem('faceAuthUser', userProfile.userName || response.userName || '');
                    localStorage.setItem('faceAuthEmail', userProfile.email || '');
                    localStorage.setItem('faceAuthFirstName', userProfile.firstName || '');
                    localStorage.setItem('faceAuthLastName', userProfile.lastName || '');
                    localStorage.setItem('faceAuthProfileImage', userProfile.profileImage || '');
                    localStorage.setItem('faceAuthEnabled', String(userProfile.faceAuthEnabled || false));

                    console.log('Face authentication successful. Complete profile loaded:', userProfile);
                  } else {
                    // Fallback to response data if profile fetch fails
                    this.storeFallbackProfileData(response);
                  }

                  this.navigateToHome();
                },
                error: (error) => {
                  console.error('Error fetching user profile:', error);
                  // Fallback to response data
                  this.storeFallbackProfileData(response);
                  this.navigateToHome();
                }
              });
            } else {
              // No email in response, use basic info
              this.storeFallbackProfileData(response);
              this.navigateToHome();
            }
          } else {
            // No token received - fall back to Keycloak login
            this.successMessage = `Face verified for ${response.userName}. Redirecting to login...`;
            console.warn('Face authentication successful but no token received. Falling back to Keycloak.');

            setTimeout(() => {
              this.keycloakService.login();
            }, 1500);
          }
        } else {
          this.errorMessage = response.message || 'Face authentication failed. Please try again.';
        }
      },
      error: (error) => {
        console.error('Face authentication error:', error);
        this.isProcessing = false;
        this.showCamera = false;

        // Extract the specific error message from the backend
        let errorMsg = 'Face authentication failed. Please try again.';

        if (error.error?.message) {
          errorMsg = error.error.message;
        } else if (error.error?.error) {
          errorMsg = error.error.error;
        } else if (error.message) {
          errorMsg = error.message;
        } else if (error.status === 400) {
          errorMsg = 'Invalid image detected. Please ensure your face is clearly visible and centered.';
        } else if (error.status === 404) {
          errorMsg = 'No matching face found. Please try again or register your face first.';
        }

        this.errorMessage = errorMsg;
        this.toastService.showError(errorMsg,'unautorized user')
      }
    });
  }

  onCaptureCancelled(): void {
    this.showCamera = false;
    this.errorMessage = '';
  }

  loginWithPassword(): void {
    this.keycloakService.login();
  }

  goToSignUp(): void {
    this.router.navigate(['/sign-up']);
  }

  private storeFallbackProfileData(response: FaceAuthResponse): void {
    localStorage.setItem('faceAuthUser', response.userName || '');
    localStorage.setItem('faceAuthEmail', response.email || '');
    localStorage.setItem('faceAuthFirstName', response.userName || '');
    localStorage.setItem('faceAuthLastName', '');
    localStorage.setItem('faceAuthProfileImage', '');
    localStorage.setItem('faceAuthEnabled', String(response.faceAuthEnabled || false));
    console.log('Face authentication successful. Token source:', response.tokenSource || 'unknown');
  }

  private navigateToHome(): void {
    setTimeout(() => {
      this.router.navigate(['/home']).then(() => {
        console.log('Navigated to home with face authentication');
      });
    }, 1500);
  }
}
