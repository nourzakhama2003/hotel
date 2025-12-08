import { Component, EventEmitter, Input, OnInit, Output, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';
import { Hotel } from '../../../models/hotel';
import { AppKeycloakService } from '../../../keycloak/services/appKeycloakService';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { UserRole } from '../../../models/enums/userRole';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-hotel-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './hotel-card.component.html',
  styleUrl: './hotel-card.component.css'
})
export class HotelCardComponent implements OnInit, OnDestroy {
  @Input() hotel!: Hotel;
  @Output() onUpdate = new EventEmitter<Hotel>();
  @Output() onDelete = new EventEmitter<number>();
  admin = false;
  private profileSubscription?: Subscription;
  private imageTimestamp = Date.now();

  constructor(private appKeycloakService: AppKeycloakService) { }

  ngOnInit(): void {
    // Subscribe to profile changes to reactively update admin status
    this.profileSubscription = this.appKeycloakService.profileObservable.subscribe(profile => {

      this.admin = profile && profile.role === UserRole.Admin ? true : false;

    });
  }



  ngOnDestroy(): void {
    // Clean up subscription to prevent memory leaks
    if (this.profileSubscription) {
      this.profileSubscription.unsubscribe();
    }
  }

  // Getter method as backup to always check current admin status
  get isAdmin(): boolean {
    const currentProfile = this.appKeycloakService.profile;
    return currentProfile && currentProfile.role === UserRole.Admin ? true : false;
  }

  // Getter method for hotel image with cache busting


  onUpdateHotel(hotel: Hotel) {
    console.log('Hotel card - updating hotel:', hotel);
    console.log('Hotel ID:', hotel.id);
    this.onUpdate.emit(hotel);
  }

  onDeleteHotel(id: number) {
    this.onDelete.emit(id);
  }



}
