import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { AppResponse } from "../models/Response";
import { Room } from "../models/room";

@Injectable({
    providedIn: 'root'
})
export class RoomSerice {
    URL = `${environment.API_URL}/public/rooms`;

    constructor(private http: HttpClient) { }

    getRooms(): Observable<AppResponse> {

        return this.http.get<AppResponse>(this.URL);
    }
    getAvailableRooms(checkInDate: string, checkOutDate: string, roomType: string): Observable<AppResponse> {
        if (roomType) {
            return this.http.get<AppResponse>(`${this.URL}/available?checkInDate=${checkInDate}&checkOutDate=${checkOutDate}&roomType=${roomType}`);
        }
        return this.http.get<AppResponse>(`${this.URL}/available?checkInDate=${checkInDate}&checkOutDate=${checkOutDate}`);
    }
    getRoomNumber(): Observable<AppResponse> {

        return this.http.get<AppResponse>(this.URL + "/number");
    }
    getRoombyId(id: number): Observable<AppResponse> {

        return this.http.get<AppResponse>(this.URL + "/" + id);
    }
    addRoom(room: Partial<Room>): Observable<AppResponse> {
        const { hotelId } = room;
        const id = Number(hotelId);
        console.log(id);
        console.log(typeof (id));
        return this.http.post<AppResponse>(this.URL + "/hotel/" + id, room);
    }

    updateRoom(id: number, roomData: Partial<Room>): Observable<AppResponse> {
        return this.http.put<AppResponse>(`${this.URL}/${id}`, roomData);
    }


    deleteRoom(id: number): Observable<AppResponse> {
        return this.http.delete<AppResponse>(`${this.URL}/${id}`);
    }

}