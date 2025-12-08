import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { BlankComponent } from './layout/blank/blank.component';
import { LoginComponent } from './pages/login/login.component';
import { Sign } from 'crypto';
import { SignUpComponent } from './pages/sign-up/sign-up.component';
import { AppComponent } from './app.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { AuthGuard } from './keycloak/guards/auth.guard';
import { FaceLoginComponent } from './pages/face-login/face-login.component';

import { RoomsComponent } from './pages/rooms/rooms.component';
import { ProfileComponent } from './components/profile/profile.component';
import { AllRoomsComponent } from './pages/all-rooms/all-rooms.component';
import { RoomDetailsComponent } from './pages/room-details/room-details.component';
import { MyBookingsComponent } from './pages/my-bookings/my-bookings.component';
import { BookingsComponent } from './pages/bookings/bookings.component';
import { PaymentComponent } from './pages/payment/payment.component';
import { PaymentSuccessComponent } from './pages/payment-success/payment-success.component';
import { PaymentFailureComponent } from './pages/payment-failure/payment-failure.component';

export const routes: Routes = [
    {
        path: '',
        component: MainLayoutComponent,
        children: [
            { path: '', component: HomeComponent },
            { path: 'home', component: HomeComponent }, // Add explicit home route,
            { path: 'hotel/rooms/:id', component: RoomsComponent },
            { path: 'hotel/room/:id', component: RoomDetailsComponent, data: { roles: ['user', 'admin'] }, canActivate: [AuthGuard] },
            { path: 'rooms', component: AllRoomsComponent },
            { path: 'payment/:bookingRefrence/:amount', component: PaymentComponent, data: { roles: ['user', 'admin'] }, canActivate: [AuthGuard] },
            { path: 'payment-success/:bookingRefrence', component: PaymentSuccessComponent, data: { roles: ['user', 'admin'] }, canActivate: [AuthGuard] },
            { path: 'payment-failure/:bookingRefrence', component: PaymentFailureComponent, data: { roles: ['user', 'admin'] }, canActivate: [AuthGuard] },
            { path: 'myBookings', component: MyBookingsComponent, data: { roles: ['user', 'admin'] }, canActivate: [AuthGuard] },
            { path: 'bookings', component: BookingsComponent, data: { roles: ['admin'] }, canActivate: [AuthGuard] },
            { path: 'dashboard', component: DashboardComponent, data: { roles: ['admin'] }, canActivate: [AuthGuard] },
            { path: 'profile', component: ProfileComponent, data: { roles: ['user'] } }
        ]
    },
    {
        path: 'auth',
        component: BlankComponent,
        children: [
            { path: 'login', component: LoginComponent },
            { path: 'face-login', component: FaceLoginComponent },
            { path: 'signup', component: SignUpComponent }
        ]
    },
];
