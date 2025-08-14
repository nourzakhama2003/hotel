import { Component, OnInit } from "@angular/core";
import { HotelService } from "../../services/hotel.service";
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [MatSnackBarModule, CommonModule],
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
    hotel!: string;
    constructor(private hotelService: HotelService, private snackBar: MatSnackBar) { }
    ngOnInit(): void {
        console.log('🏠 Home component initializing...');
        console.log('🔗 About to call hotel service...');

        this.hotelService.getHotels().subscribe({
            next: (data: string) => {

                this.hotel = data;
                this.snackBar.open('Hotel data loaded successfully!', 'Fermer', { duration: 3000 });
            },
            error: (error) => {

                this.snackBar.open(`Erreur lors du chargement du hotel (${error.status || 'Unknown'})`, 'Fermer', { duration: 3000 });
            }
        })
    }

}