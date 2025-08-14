import { Injectable } from "@angular/core";
import { KeycloakService } from 'keycloak-angular';
import { UserProfile } from "../userProfile";

@Injectable({
  providedIn: 'root'
})
export class AppKeycloakService {
  private _KeycloakService: KeycloakService;
  private _profile: UserProfile | undefined;

  constructor(private keycloakService: KeycloakService) {
    this._KeycloakService = keycloakService;
  }

  // Getter to access the keycloak instance
  get KeycloakService(): KeycloakService {
    return this._KeycloakService;
  }

  // Getter to access user profile
  get profile(): UserProfile | undefined {
    return this._profile;
  }

  async init(): Promise<boolean> {
    console.log('Starting Keycloak initialization');
    const auth = await this._KeycloakService.init({
      config: {
        url: 'http://localhost:8080',
        realm: 'hotelrealm',
        clientId: 'hotel-frontend'
      },
      initOptions: {
        onLoad: 'check-sso',
        checkLoginIframe: false,
        redirectUri: window.location.origin,
        flow: 'standard',
        pkceMethod: 'S256'
      }
    });

    if (auth) {
      console.log("User authenticated");
      await this.loadUserProfile();

      // Set up token refresh handler only for authenticated users
      this._KeycloakService.getKeycloakInstance().onTokenExpired = () => {
        console.log('Access token expired, refreshing...');
        this._KeycloakService.getKeycloakInstance().updateToken(30).then(refreshed => {
          if (refreshed) {
            console.log('Token refreshed successfully');
            // Reload user profile with new token
            this.loadUserProfile();
          } else {
            console.log('Token refresh failed, logging out...');
            this.logout();
          }
        }).catch(error => {
          console.error('Token refresh error:', error);
          this.logout();
        });
      };
    } else {
      console.log("User not authenticated");
    }

    return auth;
  }

  async loadUserProfile(): Promise<void> {
    try {
      // Get user information from the token instead of making API call
      const tokenParsed = this._KeycloakService.getKeycloakInstance().tokenParsed;
      const token = await this._KeycloakService.getToken();
      console.log('this token persed', tokenParsed);

      if (tokenParsed) {
        // Store profile from token claims
        this._profile = {
          userName: tokenParsed['preferred_username'] || '',
          email: tokenParsed['email'] || '',
          firstName: tokenParsed['given_name'] || '',
          lastName: tokenParsed['family_name'] || '',
          token: token,
          roles: tokenParsed['realm_access']?.roles || []
        } as UserProfile;

        console.log('User profile loaded from token:', this._profile);
      } else {
        console.warn('No token parsed available');
      }
    } catch (error) {
      console.error('Failed to load user profile:', error);
    }
  }

  // Helper methods for authentication
  login(): void {
    this._KeycloakService.login({ redirectUri: window.location.origin + '/home' });
  }

  logout(): void {
    this._KeycloakService.logout(window.location.origin);
  }
  signUp() {
    this._KeycloakService.register({ redirectUri: window.location.origin + '/home' });

  }

  isLoggedIn(): boolean {
    return this._KeycloakService.isLoggedIn();
  }

  getToken(): Promise<string> {
    return this._KeycloakService.getToken();
  }

  hasRealmRole(role: string): boolean {
    return this._KeycloakService.isUserInRole(role);
  }

  hasResourceRole(role: string, resource?: string): boolean {
    return this._KeycloakService.isUserInRole(role, resource);
  }
}