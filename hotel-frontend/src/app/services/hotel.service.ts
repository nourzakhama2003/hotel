import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment";
import { AppResponse } from "../models/Response";
import { Hotel } from "../models/hotel";
@Injectable({ providedIn: 'root' })
export class HotelService {
    URL = `${environment.API_URL}/public/hotels`;

    constructor(private http: HttpClient) { }
    getHotels(): Observable<AppResponse> {

        return this.http.get<AppResponse>(this.URL);
    }
    getHotelbyId(id: number): Observable<AppResponse> {

        return this.http.get<AppResponse>(this.URL + "/" + id);
    }
    addHotel(hotel: Partial<Hotel>): Observable<AppResponse> {
        return this.http.post<AppResponse>(this.URL, hotel);
    }

    updateHotel(id: number, hotelData: Partial<Hotel>): Observable<AppResponse> {
        return this.http.put<AppResponse>(`${this.URL}/${id}`, hotelData);
    }


    deleteHotel(id: number): Observable<AppResponse> {
        return this.http.delete<AppResponse>(`${this.URL}/${id}`);
    }

}