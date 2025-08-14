// import { Routes } from '@angular/router';
// import { AuthGuard } from './keycloak/guards/auth.guard';
// import { AdminGuard } from './keycloak/guards/admin.guard';

// export const routes: Routes = [
//     // Public routes (no authentication required)
//     {
//         path: '',
//         redirectTo: '/home',
//         pathMatch: 'full'
//     },
//     {
//         path: 'home',
//         loadComponent: () => import('./components/home/home.component').then(m => m.HomeComponent)
//     },
//     {
//         path: 'login',
//         loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent)
//     },

//     // Protected routes (authentication required)
//     {
//         path: 'dashboard',
//         loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent),
//         canActivate: [AuthGuard]  // 🛡️ Only authenticated users
//     },
//     {
//         path: 'profile',
//         loadComponent: () => import('./components/profile/profile.component').then(m => m.ProfileComponent),
//         canActivate: [AuthGuard]  // 🛡️ Only authenticated users
//     },
//     {
//         path: 'bookings',
//         loadComponent: () => import('./components/bookings/bookings.component').then(m => m.BookingsComponent),
//         canActivate: [AuthGuard]  // 🛡️ Only authenticated users
//     },

//     // Admin-only routes (authentication + admin role required)
//     {
//         path: 'admin',
//         children: [
//             {
//                 path: 'users',
//                 loadComponent: () => import('./components/admin/users/users.component').then(m => m.UsersComponent),
//                 canActivate: [AdminGuard]  // 👑 Only admin users
//             },
//             {
//                 path: 'settings',
//                 loadComponent: () => import('./components/admin/settings/settings.component').then(m => m.SettingsComponent),
//                 canActivate: [AdminGuard]  // 👑 Only admin users
//             },
//             {
//                 path: 'reports',
//                 loadComponent: () => import('./components/admin/reports/reports.component').then(m => m.ReportsComponent),
//                 canActivate: [AdminGuard]  // 👑 Only admin users
//             }
//         ]
//     },

//     // Fallback route
//     {
//         path: '**',
//         redirectTo: '/home'
//     }
// ];
