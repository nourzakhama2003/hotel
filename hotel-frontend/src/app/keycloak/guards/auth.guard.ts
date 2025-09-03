import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, GuardResult, MaybeAsync, RouterStateSnapshot, Router } from "@angular/router";
import { AppKeycloakService } from "../services/appKeycloakService";

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {
    constructor(
        private appKeycloakService: AppKeycloakService,
        private router: Router
    ) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
      
        if (!this.appKeycloakService.isLoggedIn()) {
            this.appKeycloakService.login();
            return false;
        }

  
        if (!this.appKeycloakService.profile) {
            this.appKeycloakService.login();
            return false;
        }

        const routeRoles: string[] = route.data['roles'] || [];
        const userRoles: string = this.appKeycloakService?.profile?.role || "";


        if (routeRoles.length === 0) {
            return true;
        }

      
        const hasAccess = routeRoles.some(
            role => userRoles.toLowerCase() === role.toLowerCase()
        );
        if (hasAccess) {
            return true;
        } else {
            
            alert('Access denied. You do not have permission to access this page.');
            this.router.navigate(['/']); 
            return false;
        }
    }

}