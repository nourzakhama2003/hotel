import { Component, OnInit, HostListener } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { links } from '../../constant/constants';
import { CommonModule } from '@angular/common';
import { AppKeycloakService } from '../../keycloak/services/appKeycloakService';
@Component({
  selector: 'app-header',
  imports: [RouterLink, CommonModule, RouterLinkActive],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit {
  links = links;
  isProfileDropdownOpen = false;

  constructor(private appKeycloakService: AppKeycloakService) { }

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

  // @HostListener('document:click', ['$event'])
  // onDocumentClick(event: Event) {
  //   const target = event.target as HTMLElement;
  //   const profileButton = target.closest('.profile-button');
  //   const dropdown = target.closest('.profile-dropdown');

  //   if (!profileButton && !dropdown) {
  //     this.isProfileDropdownOpen = false;
  //   }
  // }




  @HostListener('document:click', ['$event'])
  closeDropdown(event: Event) {
    const target = event.target as HTMLElement;
    const button = target.closest('.profile-button');
    const dropdown = target.closest('.profile-dropdown');
    if (!button && !dropdown) {
      this.isProfileDropdownOpen = false;
    }
  }

}
