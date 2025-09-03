import { Component, OnInit } from "@angular/core";
import { HotelService } from "../../services/hotel.service";
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { AppResponse } from "../../models/Response";
import { hotel } from "../../models/hotel";
@Component({
    selector: 'app-home',
    standalone: true,
    imports: [MatSnackBarModule, CommonModule],
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
    isLoading=false;
    message="";
    hotels!: hotel[] | undefined;
    constructor(private hotelService: HotelService, private snackBar: MatSnackBar) { }
    ngOnInit(): void {
        this.loadHotels();

    }



    loadHotels(){
        this.isLoading=true;
        this.hotelService.getHotels().subscribe({
           next:(response:AppResponse)=>{
            this.hotels=response.hotels;
            this.isLoading=false;
           } ,
           error:(err)=>{
            this.message=err.error.message|| 'can t get hotels';
            this.isLoading=false;

           }
        })
    }

}