import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { AppKeycloakService } from "../services/appKeycloakService";

@Injectable({
    providedIn: 'root'
})
export class AuthInterceptor implements HttpInterceptor {
    constructor(private appKeycloakService: AppKeycloakService) { }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {


        if (req.url.includes('/auth/') || req.url.includes('/realms/')) {
        
            return next.handle(req);
        }

        const isLoggedIn = this.appKeycloakService.isLoggedIn();
    

        if (!this.appKeycloakService.isLoggedIn()) {
           
            return next.handle(req);
        }

        const token = this.appKeycloakService.profile?.token;
   

        if (!token) {
            return next.handle(req);
        }

        const authreq = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
     
        return next.handle(authreq);
    }

}