import { Injectable } from "@angular/core";
import { AppResponse } from "../models/Response";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment";
import { Booking } from "../models/booking";



@Injectable({
    providedIn: 'root'
})
export class BookingService {
    /*trigger change*/
    URL = `${environment.API_URL}/public/bookings`;

    constructor(private http: HttpClient) { }
    getBookings(): Observable<AppResponse> {

        return this.http.get<AppResponse>(this.URL);
    }
    getBookingbyId(id: number): Observable<AppResponse> {

        return this.http.get<AppResponse>(this.URL + "/" + id);
    }
    getBookingbyUserId(id: number): Observable<AppResponse> {

        return this.http.get<AppResponse>(this.URL + "/user/" + id);
    }

    getBookingByReference(reference: string): Observable<AppResponse> {
        return this.http.get<AppResponse>(this.URL + "/reference?ref=" + reference);
    }

    addBooking(booking: Partial<Booking>): Observable<AppResponse> {
        return this.http.post<AppResponse>(`${this.URL}`, booking);
    }

    updatebooking(id: number, bookingData: Partial<Booking>): Observable<AppResponse> {
        return this.http.put<AppResponse>(`${this.URL}/${id}`, bookingData);
    }

    updatebookingByRefrence(bookingRefrence: string, bookingData: Partial<Booking>): Observable<AppResponse> {
        return this.http.put<AppResponse>(`${this.URL}/bookingRefrence/${bookingRefrence}`, bookingData);
    }


    deleteHotel(id: number): Observable<AppResponse> {
        return this.http.delete<AppResponse>(`${this.URL}/${id}`);
    }

}