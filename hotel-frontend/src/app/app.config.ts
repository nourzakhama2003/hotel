import { ApplicationConfig, provideZoneChangeDetection, APP_INITIALIZER, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors, HTTP_INTERCEPTORS, withInterceptorsFromDi } from '@angular/common/http';
import { AppKeycloakService } from './keycloak/services/appKeycloakService';
import { AuthInterceptor } from './keycloak/interceptors/auth.interceptor';
import { routes } from './app.routes';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';


import { ToastrModule } from 'ngx-toastr';
export function initializekeycloak(appKeycloakService: AppKeycloakService) {
  return () => {
    return appKeycloakService.init();
  };
}
export const appConfig: ApplicationConfig = {
  providers: [


    importProvidersFrom(ToastrModule.forRoot({
      timeOut: 3000,
      positionClass: 'toast-bottom-center',
      preventDuplicates: true,
    })),


    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()), // This enables class-based interceptors
    provideAnimationsAsync(), // Required for Material components

    KeycloakService,
    {
      provide: APP_INITIALIZER,
      useFactory: initializekeycloak,
      deps: [AppKeycloakService],
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ]
};
