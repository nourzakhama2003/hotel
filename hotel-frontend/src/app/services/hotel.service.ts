import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../enviorments/enviorment";
import { AppResponse } from "../constant/Response";
@Injectable({ providedIn: 'root' })
export class HotelService {
        URL = `${environment.API_URL}/public/hotels`;
  
    constructor(private http: HttpClient) { }
    getHotels(): Observable<AppResponse> {
        
        return this.http.get<AppResponse>(this.URL);
    }

}