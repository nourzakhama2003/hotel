import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { Room } from '../../../models/room';
import { CommonModule } from '@angular/common';
import { RoomcardComponent } from '../room-card/room-card.component';

@Component({
  selector: 'app-room-list',
  standalone: true,
  imports: [CommonModule, RoomcardComponent],
  templateUrl: './room-list.component.html',
  styleUrl: './room-list.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RoomListComponent {
  @Input() rooms!: Room[];
  @Input() allRooms = true;
  @Output() onUpdate = new EventEmitter<Room>();
  @Output() onDelete = new EventEmitter<number>();

  update(room: Room) {
    this.onUpdate.emit(room);
  }
  deleteRoom(id: number) {
    this.onDelete.emit(id);
  }
}
