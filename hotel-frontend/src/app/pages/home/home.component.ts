import { Component, OnInit, OnDestroy } from "@angular/core";
import { HotelService } from "../../services/hotel.service";
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AppResponse } from "../../models/Response";
import { Hotel } from "../../models/hotel";
import { ToastService } from "../../services/toast.service";
import { HotelListComponent } from "../../components/hotel/hotel-list/hotel-list.component";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { HotelFormComponent } from "../../components/hotel/hotel-form/hotel-form.component";
import { AppKeycloakService } from "../../keycloak/services/appKeycloakService";
import { SpinnerComponent } from "../../components/constants/spinner/spinner.component";
import { ChatbotComponent } from "../../components/chatbot/chatbot.component";
import { Subscription } from 'rxjs';
import { UserRole } from "../../models/enums/userRole";
import { Router } from '@angular/router';
import { ConfirmDialogComponent } from "../../components/confirmDialog/confirm-dialog.component";

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [MatSnackBarModule, CommonModule, FormsModule, HotelListComponent, MatDialogModule, SpinnerComponent, ChatbotComponent],
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
    admin = false;
    isLoading = false;
    searchItem = '';
    hotels: Hotel[] = [];
    allHotels: Hotel[] = []; // Store the original unfiltered list
    private profileSubscription?: Subscription;

    constructor(private hotelService: HotelService, private toastService: ToastService, private matDialog: MatDialog, private AppkeyCloakService: AppKeycloakService, private router: Router) { }

    ngOnInit(): void {
        // Subscribe to profile changes to reactively update admin status
        this.profileSubscription = this.AppkeyCloakService.profileObservable.subscribe(profile => {
            this.admin = profile && profile.role === UserRole.Admin ? true : false;
        });

        // Load hotels after Keycloak initializes (via observable subscription)
        // This prevents the authentication error on first load
        const initCheckInterval = setInterval(() => {
            if (this.AppkeyCloakService.isInitialized) {
                clearInterval(initCheckInterval);
                this.loadHotels();
            }
        }, 100); // Check every 100ms until initialized

        // Safety timeout - stop checking after 5 seconds
        setTimeout(() => clearInterval(initCheckInterval), 5000);
    }

    ngOnDestroy(): void {
        // Clean up subscription to prevent memory leaks
        if (this.profileSubscription) {
            this.profileSubscription.unsubscribe();
        }
    }

    // Getter method as backup to always check current admin status
    get isAdmin(): boolean {
        const currentProfile = this.AppkeyCloakService.profile;
        return currentProfile && currentProfile.role === UserRole.Admin ? true : false;
    }

    // Date helper methods for search form
    getCurrentDate(): string {
        const today = new Date();
        return today.toISOString().split('T')[0];
    }

    getTomorrowDate(): string {
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        return tomorrow.toISOString().split('T')[0];
    }


    searchHotel() {
        if (this.allHotels && this.allHotels.length > 0) {
            if (this.searchItem && this.searchItem.trim() !== '') {
                // Filter hotels based on search term (case-insensitive)
                this.hotels = this.allHotels.filter(hotel =>
                    hotel.hotelName.toLowerCase().includes(this.searchItem.toLowerCase())
                );
            } else {
                // If search is empty, show all hotels
                this.hotels = [...this.allHotels];
            }
        }
    }

    // Method to handle search input changes
    onSearchChange(searchValue: string) {
        this.searchItem = searchValue;
        this.searchHotel();
    }

    // Method to clear search
    clearSearch() {
        this.searchItem = '';
        this.hotels = [...this.allHotels];
    }
    loadHotels() {
        this.isLoading = true;
        this.hotelService.getHotels().subscribe({
            next: (response: AppResponse) => {
                this.allHotels = response.hotels || []; // Store original data
                this.hotels = [...this.allHotels]; // Copy for display
                console.log('Loaded hotels:', this.hotels);
                console.log('Hotel IDs:', this.hotels.map(h => ({ name: h.hotelName, id: h.id })));
                this.isLoading = false; // Set to false when data is loaded

                // Show success toast
                // this.toastService.success(`Successfully loaded ${this.hotels.length} hotels! ✅`, 'Success', {
                //   positionClass: 'toast-b',
                //   toastClass:'toast-s',
                //   timeOut: 3000
                // });
            },
            error: (err) => {
                this.isLoading = false;
                // Don't show error if it's null (suppressed during initialization)
                if (err !== null && err.error?.message) {
                    this.toastService.showError(`${err.error.message} ❌`, 'Error');
                }
            }
        });
    }


    addHotel(hotel?: Hotel) {
        const addRef = this.matDialog.open(HotelFormComponent, { width: '60%', height: '80%', maxHeight: '90vh', data: hotel || {}, autoFocus: false });
        addRef.afterClosed().subscribe({
            next: (hotelData) => {
                if (hotelData) {
                    // Adding new hotel
                    if (!hotel) {
                        this.hotelService.addHotel(hotelData).subscribe({
                            next: (response: AppResponse) => {
                                this.hotels = [...this.hotels, response.hotel!];
                                this.toastService.showSuccess(`${response?.message}`);
                            },
                            error: (err) => {
                                this.toastService.showError(`${err.error?.message}`, 'Error while adding hotel');
                            }
                        });
                    } else {
                        if (!hotel?.id || hotel.id === undefined) {
                            this.toastService.showError('Hotel ID is missing. Cannot update hotel.', 'Error');
                            return;
                        }

                        this.hotelService.updateHotel(hotel.id, hotelData).subscribe({
                            next: (response: AppResponse) => {
                                // For updates, we should update the existing hotel in the array, not add a new one
                                const hotelIndex = this.hotels.findIndex(h => h.id === hotel.id);
                                if (hotelIndex !== -1 && response.hotel) {
                                    this.hotels[hotelIndex] = response.hotel;
                                    this.hotels = [...this.hotels]; // Trigger change detection
                                }
                                this.toastService.showSuccess(`${response?.message}`);
                            },
                            error: (err) => {
                                this.toastService.showError(`${err.error?.message}`, 'Error while updating hotel');
                            }
                        });

                    }
                }
            },
            error: () => {
                this.toastService.showError('An error occurred while closing the dialog');
            }
        });
    }
    update(hotel: Hotel) {
        this.addHotel(hotel); // Reuse the addHotel method for editing
    }
    onDeleteHotel(id: number) {
        console.log('Before delete - Admin status:', this.admin);
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
                    this.isLoading = true;
                    this.hotelService.deleteHotel(id).subscribe({
                        next: (response: AppResponse) => {
                            this.hotels = this.hotels.filter((hotel) => hotel.id != id);
                            this.isLoading = false;
                            this.toastService.showSuccess(`${response.message}`, 'success');
                        },
                        error: (err) => {
                            this.isLoading = false;
                            this.toastService.showError(`${err.error?.message}`);
                        }
                    });
                }

            },
            error: () => {
                this.toastService.showError('An error occurred while closing the dialog');
            }
        })

    }

    /**
     * Smooth scroll to the hotels section below the hero
     */
    exploreHotels() {
        // First, ensure hotels are loaded
        if (this.hotels.length === 0 && !this.isLoading) {
            this.loadHotels();
        }

        // Use a small delay to ensure content is rendered, then scroll
        setTimeout(() => {
            const hotelsSection = document.getElementById('hotels-section');
            if (hotelsSection) {
                hotelsSection.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            } else {
                // Fallback: try to find the hotel-list component
                const hotelList = document.querySelector('app-hotel-list');
                if (hotelList) {
                    hotelList.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                } else {
                    // Final fallback: scroll to bottom
                    window.scrollTo({
                        top: document.body.scrollHeight,
                        behavior: 'smooth'
                    });
                }
            }
        }, 100);
    }

}