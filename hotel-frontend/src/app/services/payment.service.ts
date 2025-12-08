import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment";
import { AppResponse } from "../models/Response";
import { Hotel } from "../models/hotel";
import { Payment } from "../models/payment";
@Injectable({ providedIn: 'root' })
export class PaymentService {
    URL = `${environment.API_URL}/public/payments`;

    constructor(private http: HttpClient) { }
    getPayments(): Observable<AppResponse> {

        return this.http.get<AppResponse>(this.URL);
    }
    getPaymentbyId(id: number): Observable<AppResponse> {

        return this.http.get<AppResponse>(this.URL + "/" + id);
    }
    initialisePayment(payment: Partial<Payment>): Observable<AppResponse> {
        return this.http.post<AppResponse>(this.URL, payment);
    }

    createPayment(payment: Partial<Payment>): Observable<any> {
        return this.http.post<any>(`${this.URL}/create`, payment);
    }

    updateHotel(id: number, payment: Partial<Payment>): Observable<AppResponse> {
        return this.http.put<AppResponse>(`${this.URL}/${id}`, payment);
    }


    deleteHotel(id: number): Observable<AppResponse> {
        return this.http.delete<AppResponse>(`${this.URL}/${id}`);
    }

}