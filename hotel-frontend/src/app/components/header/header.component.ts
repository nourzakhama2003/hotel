import { Component, OnInit, HostListener } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { links } from '../../constant/constants';
import { CommonModule } from '@angular/common';
import { AppKeycloakService } from '../../keycloak/services/appKeycloakService';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProfileComponent } from '../profile/profile.component';
import { UserService } from '../../services/user.service';
@Component({
  selector: 'app-header',
  imports: [RouterLink, CommonModule, RouterLinkActive, MatDialogModule, MatSnackBarModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit {
  links = links;
  isProfileDropdownOpen = false;

  constructor(private appKeycloakService: AppKeycloakService, private matDialog: MatDialog, private userService: UserService, private matSnackBar: MatSnackBar) { }

  ngOnInit(): void {
    // Initial auth state check
  }

  // Use a getter to always get the current auth state
  get isAuthenitcatedUser(): boolean {
    return this.appKeycloakService.isLoggedIn();
  }

  login() {
    this.appKeycloakService.login();
  }

  signUp() {
    this.appKeycloakService.signUp();
  }

  logout() {
    this.appKeycloakService.logout();
    this.isProfileDropdownOpen = false; // Close dropdown after logout
  }

  toggleProfileDropdown() {
    console.log('Toggle clicked! Current state:', this.isProfileDropdownOpen);
    this.isProfileDropdownOpen = !this.isProfileDropdownOpen;
    console.log('New state:', this.isProfileDropdownOpen);
  }





  @HostListener('document:click', ['$event'])
  closeDropdown(event: Event) {
    const target = event.target as HTMLElement;
    const button = target.closest('.profile-button');
    const dropdown = target.closest('.profile-dropdown');
    if (!button && !dropdown) {
      this.isProfileDropdownOpen = false;
    }
  }


  openDialog() {
    // First, get the username from Keycloak profile
    const username = this.appKeycloakService.profile?.userName;

    if (!username) {
      this.matSnackBar.open('Erreur: nom d\'utilisateur introuvable', 'Fermer', { duration: 3000 });
      return;
    }

    // Fetch complete user profile from backend (includes profileImage)
    this.userService.getUserByUsername(username).subscribe({
      next: (backendProfile) => {
        console.log('Complete profile loaded from backend:', backendProfile);

        // Merge Keycloak profile with backend profile (prioritize backend data)
        const completeProfile = {
          ...this.appKeycloakService.profile,
          ...backendProfile,
          // Ensure token remains from Keycloak
          token: this.appKeycloakService.profile?.token
        };

        console.log('Opening dialog with complete profile:', completeProfile);

        // Open dialog with complete profile data
        const dialogRef = this.matDialog.open(ProfileComponent, {
          width: "40%",
          height: "85%",
          data: completeProfile
        });

        dialogRef.afterClosed().subscribe(result => {
          if (result) {
            console.log("update", result);

            // Use username to update profile (more reliable than ID)
            const username = result.userName || this.appKeycloakService.profile?.userName;

            if (username) {
              // Use updateUserByUsername for profile changes including profileImage
              this.userService.updateUserByUsername(username, result).subscribe({
                next: user => {
                  this.matSnackBar.open('user profile mis à jour avec succès', 'Fermer', { duration: 3000 });
                  console.log('Profile updated successfully:', user);
                },
                error: (error) => {
                  console.error('Profile update error:', error);
                  this.matSnackBar.open('problème lors de mise à jour de profile', 'Fermer', { duration: 3000 });
                }
              });
            } else {
              console.error('No username found for profile update');
              this.matSnackBar.open('erreur: nom d\'utilisateur introuvable', 'Fermer', { duration: 3000 });
            }
          }
          else {
            console.log("close");
          }
        });
      },
      error: (error) => {
        console.error('Failed to load backend profile:', error);
        // Fallback to Keycloak profile if backend fails
        console.log('Falling back to Keycloak profile');
        const dialogRef = this.matDialog.open(ProfileComponent, {
          width: "40%",
          height: "85%",
          data: this.appKeycloakService.profile || {}
        });

        // Handle dialog close for fallback case
        dialogRef.afterClosed().subscribe(result => {
          if (result) {
            console.log("update", result);
            const username = result.userName || this.appKeycloakService.profile?.userName;
            if (username) {
              this.userService.updateUserByUsername(username, result).subscribe({
                next: user => {
                  this.matSnackBar.open('user profile mis à jour avec succès', 'Fermer', { duration: 3000 });
                  console.log('Profile updated successfully:', user);
                },
                error: (error) => {
                  console.error('Profile update error:', error);
                  this.matSnackBar.open('problème lors de mise à jour de profile', 'Fermer', { duration: 3000 });
                }
              });
            }
          }
        });
      }
    });
  }

}
