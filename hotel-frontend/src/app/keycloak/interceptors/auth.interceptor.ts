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
        console.log('🔍 Interceptor called for URL:', req.url);

        if (req.url.includes('/auth/') || req.url.includes('/realms/')) {
            console.log('⏭️ Skipping auth endpoints');
            return next.handle(req);
        }

        const isLoggedIn = this.appKeycloakService.isLoggedIn();
        console.log('🔐 User logged in?', isLoggedIn);

        if (!this.appKeycloakService.isLoggedIn()) {
            console.log('❌ User not logged in, sending request without token');
            return next.handle(req);
        }

        const token = this.appKeycloakService.profile?.token;
        console.log('🎫 Token exists?', !!token);
        console.log('🎫 Token preview:', token ? token.substring(0, 50) + '...' : 'No token');
        console.log('📋 Profile object:', this.appKeycloakService.profile);

        if (!token) {
            console.log('❌ No token available, sending request without auth');
            return next.handle(req);
        }

        const authreq = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
        console.log('✅ Adding Authorization header to request');
        console.log('📨 Authorization header:', authreq.headers.get('Authorization')?.substring(0, 50) + '...');

        return next.handle(authreq);
    }

}