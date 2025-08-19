import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class HotelService {
    constructor(private http: HttpClient) { }
    getHotels(): Observable<string> {
        console.log('🏨 HotelService: Making request to http://localhost:8081/api/user/hotels');
        return this.http.get('http://localhost:8081/api/public/hotels', {
            responseType: 'text' // Expecting plain text, not JSON
        });
    }

}