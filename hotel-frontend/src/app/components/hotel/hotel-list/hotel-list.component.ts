import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { HotelService } from '../../../services/hotel.service';
import { ToastrService } from 'ngx-toastr';
import { Hotel } from '../../../models/hotel';
import { AppResponse } from '../../../models/Response';
import { SpinnerComponent } from "../../constants/spinner/spinner.component";
import { CommonModule } from '@angular/common';
import { HotelCardComponent } from '../hotel-card/hotel-card.component';


@Component({
  selector: 'app-hotel-list',
  standalone: true,
  imports: [CommonModule, HotelCardComponent],
  templateUrl: './hotel-list.component.html',
  styleUrl: './hotel-list.component.css'
})
export class HotelListComponent implements OnInit {
  @Input() hotels!: Hotel[];
  @Output() onUpdate = new EventEmitter<Hotel>();
  @Output() onDelete = new EventEmitter<number>();

  constructor(private hotelservice: HotelService, private toastService: ToastrService) { }


  ngOnInit(): void {

  }

  trackByHotelId(index: number, hotel: Hotel): number {
    return hotel.id;
  }

  onUpdateHotel(hotel: Hotel) {
    this.onUpdate.emit(hotel);
  }

  onDeleteHotel(id: number) {
    this.onDelete.emit(id);
  }
}
