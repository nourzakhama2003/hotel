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



    updateUserProfile(id: any, profile: Partial<UserProfile>): Observable<Partial<UserProfile>> {
        return this.http.put<Partial<UserProfile>>(`${this.URL}/${id}`, profile);
    }

    updateUserByUsername(username: string, profile: Partial<UserProfile>): Observable<UserProfile> {
        // Convert UserProfile to UserUpdateDto format expected by backend
        const updateData = {
            userName: profile.userName,
            email: profile.email,
            firstName: profile.firstName,
            lastName: profile.lastName,
            role: profile.role,
            profileImage: profile.profileImage
        };

        console.log('Sending update data to backend:', updateData);
        return this.http.put<UserProfile>(`${this.URL}/username/${username}`, updateData);
    }

    getUserByUsername(username: string): Observable<UserProfile> {
        return this.http.get<UserProfile>(`${this.URL}/username/${username}`);
    }

    getUserByEmail(email: string): Observable<UserProfile> {
        return this.http.get<UserProfile>(`${this.URL}/email/${email}`);
    }
}