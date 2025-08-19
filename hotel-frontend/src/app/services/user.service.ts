import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { UserProfile } from "../keycloak/userProfile";
import { environment } from "../../enviorments/enviorment";

@Injectable({
    providedIn: 'root'
})
export class UserService {
    URL = `${environment.API_URL}/public/users`;
    constructor(private http: HttpClient) { }

    getAll(): Observable<UserProfile[]> {
        return this.http.get<UserProfile[]>(this.URL);

    }


    add(userProfile: Partial<UserProfile>): Observable<UserProfile> {
        return this.http.post<UserProfile>(this.URL, userProfile);
    }

    syncFromKeycloak(userProfile: Partial<UserProfile>): Observable<UserProfile> {
        return this.http.post<UserProfile>(`${this.URL}/sync`, userProfile);
    }
}