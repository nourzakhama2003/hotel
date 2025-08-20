import { Component, OnInit, HostListener, OnDestroy } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { links } from '../../constant/constants';
import { CommonModule } from '@angular/common';
import { AppKeycloakService } from '../../keycloak/services/appKeycloakService';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProfileComponent } from '../profile/profile.component';
import { UserService } from '../../services/user.service';
import { Subscription } from 'rxjs';
@Component({
  selector: 'app-header',
  imports: [RouterLink, CommonModule, RouterLinkActive, MatDialogModule, MatSnackBarModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit, OnDestroy {
  links = links;
  isProfileDropdownOpen = false;
  profileImage!: string;
  private profileSubscription?: Subscription;

  constructor(private appKeycloakService: AppKeycloakService, private matDialog: MatDialog, private userService: UserService, private matSnackBar: MatSnackBar) { }

  ngOnInit(): void {
    console.log("tets profile image", this.appKeycloakService.profile?.profileImage);

    // Subscribe to profile changes to get the profileImage when it's available
    this.profileSubscription = this.appKeycloakService.profileObservable.subscribe(profile => {
      if (profile?.profileImage) {

        this.profileImage = profile.profileImage;
      }
    });
  }

  ngOnDestroy(): void {
    // Clean up subscription
    if (this.profileSubscription) {
      this.profileSubscription.unsubscribe();
    }
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
    const dialogRef = this.matDialog.open(ProfileComponent, {
      width: "40%",
      height: "85%",
      data: this.appKeycloakService.profile
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result.userName) {
        
        this.userService.updateUserByUsername(result.userName, result).subscribe({
          next: user => {
         
            this.matSnackBar.open('user profile mis à jour avec succès', 'Fermer', { duration: 3000 });
          },
          error: (error) => {
            this.matSnackBar.open('erreur lors de la mise à jour du profil', 'Fermer', { duration: 3000 });
          }
        });
      } else {
        console.log('Dialog was closed without updating (result is null or no userName)');
      }
    });
  }
}