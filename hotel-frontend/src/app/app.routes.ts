import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { DetailsComponent } from './pages/details/details.component';
import { BlankComponent } from './layout/blank/blank.component';
import { LoginComponent } from './pages/login/login.component';
import { Sign } from 'crypto';
import { SignUpComponent } from './pages/sign-up/sign-up.component';
import { AppComponent } from './app.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { AuthGuard } from './keycloak/guards/auth.guard';
import { ProfileComponent } from './pages/profile/profile.component';

export const routes: Routes = [
    {
        path: '',
        component: MainLayoutComponent,
        children: [
        // Default redirect
            { path: 'home', component: HomeComponent },
            { path: 'details', component: DetailsComponent, data: { roles: ['admin'] }, canActivate: [AuthGuard] },
            {path:'profile',component:ProfileComponent ,data:{roles:['user']}}
        ]
    },
    {
        path: 'auth',
        component: BlankComponent,
        children: [
            { path: 'login', component: LoginComponent },
            { path: 'signup', component: SignUpComponent }
        ]
    },
];
