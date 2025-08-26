import { Injectable } from "@angular/core";
import { KeycloakService } from 'keycloak-angular';
import { UserProfile } from "../../constant/userProfile";
import { UserService } from "../../services/user.service";
import { BehaviorSubject, Observable } from 'rxjs';
import { AppResponse } from "../../constant/Response";
@Injectable({
  providedIn: 'root'
})
export class AppKeycloakService {
  private _KeycloakService: KeycloakService;
  private _profile: Partial<UserProfile> | undefined;
  private _profileSubject = new BehaviorSubject<Partial<UserProfile> | undefined>(undefined);

  constructor(private keycloakService: KeycloakService, private userService: UserService) {
    this._KeycloakService = keycloakService;
  }

  // Getter to access the keycloak instance
  get KeycloakService(): KeycloakService {
    return this._KeycloakService;
  }

  // Getter to access user profile
  get profile(): Partial<UserProfile> | undefined {
    return this._profile;
  }

  // Observable to track profile changes
  get profileObservable(): Observable<Partial<UserProfile> | undefined> {
    return this._profileSubject.asObservable();
  }

  async init(): Promise<boolean> {
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

      await this.loadUserProfile();

      // Set up token refresh handler only for authenticated users
      this._KeycloakService.getKeycloakInstance().onTokenExpired = () => {

        this._KeycloakService.getKeycloakInstance().updateToken(30).then(refreshed => {
          if (refreshed) {


            this.loadUserProfile();
          } else {

            this.logout();
          }
        }).catch(error => {

          this.logout();
        });
      };
    } else {

    }

    return auth;
  }

  async loadUserProfile(): Promise<void> {
    try {
      // Get user information from the token instead of making API call
      const tokenParsed = this._KeycloakService.getKeycloakInstance().tokenParsed;
      const token = await this._KeycloakService.getToken();



      if (tokenParsed) {
        const role = tokenParsed['realm_access']?.roles || "";
        let userRole = "User"; // Match your backend enum exactly
        if (role.includes("admin")) {
          userRole = "Admin"; // Match your backend enum exactly
        }

        this._profile = {
          userName: tokenParsed['preferred_username'] || tokenParsed['sub'] || '',
          email: tokenParsed['email'] || '',
          firstName: tokenParsed['given_name'] || '',
          lastName: tokenParsed['family_name'] || '',
          token: token,
          role: userRole,
        } as Partial<UserProfile>;

        if (this._profile?.userName) {
          // First check if user exists in database
          this.userService.getUserByUsername(this._profile.userName).subscribe({
            next: (response: AppResponse) => {
              // User exists, use database profile data
              console.log("User found in database, using database profile");
              this._profile = { ...this._profile, ...response?.user };
              this._profileSubject.next(this._profile);
            },
            error: (error: any) => {
              if (error.status === 404) {
                // User doesn't exist in database, try to create them
                console.log("User not found in database, attempting to create");
                if (this._profile) {
                  this.userService.createOrUpdateUser(this._profile).subscribe({
                    next: (createResponse: AppResponse) => {
                      console.log("User successfully created in database");
                      this._profile = createResponse?.user;
                      this._profileSubject.next(this._profile);
                    },
                    error: (createError: any) => {
                      console.warn('Failed to create user in database, using Keycloak profile only:', createError);
                      this._profileSubject.next(this._profile);
                    }
                  });
                }
              } else {
                // Other error, just use Keycloak profile
                console.warn('Database error, using Keycloak profile only:', error);
                this._profileSubject.next(this._profile);
              }
            }
          });
        } else {
          // No username available, just use what we have
          this._profileSubject.next(this._profile);
        }


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

  /**
   * Updates the local profile data and notifies subscribers
   * @param updatedProfile The updated profile data from backend
   */
  updateLocalProfile(updatedProfile: Partial<UserProfile>): void {
    this._profile = { ...this._profile, ...updatedProfile };
    this._profileSubject.next(this._profile);
    console.log('Local profile updated:', this._profile);
  }

  /**
   * Reloads user profile from Keycloak after updates
   * This method refreshes the token and reloads profile data
   */
  // async reloadUserProfile(): Promise<void> {
  //   try {
  //     // Force token refresh to get updated claims from Keycloak
  //     const refreshed = await this._KeycloakService.getKeycloakInstance().updateToken(5);

  //     if (refreshed) {
  //       console.log('Token refreshed successfully');
  //     } else {
  //       console.log('Token is still valid');
  //     }

  //     // Reload the user profile with fresh token data
  //     await this.loadUserProfile();

  //   } catch (error) {
  //     console.error('Failed to refresh token and reload profile:', error);
  //     // If refresh fails, try to reload profile anyway
  //     await this.loadUserProfile();
  //   }
  // }
}