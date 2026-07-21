import { Routes } from '@angular/router';

import {authGuard, roleGuard} from '../core/guards/auth.guard';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('../features/auth/login/login.component').then(m => m.LoginComponent) },
  { path: 'forgot-password', loadComponent: () => import('../features/auth/login/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent) },
  { path: 'reset-password', loadComponent: () => import('../features/auth/login/reset-password/reset-password.component').then(m => m.ResetPasswordComponent) },

  {
    path: 'register',
    children: [
      { path: 'role', loadComponent: () => import('../features/auth/role-selection/role-selection.component').then(m => m.RoleSelectionComponent) },
      { path: 'donor', loadComponent: () => import('../features/auth/donor-form/donor-form.component').then(m => m.DonorFormComponent) },
      { path: 'driver', loadComponent: () => import('../features/auth/driver-form/driver-form.component').then(m => m.DriverFormComponent) },
      { path: 'ngo', loadComponent: () => import('../features/auth/ngo-form/ngo-form.component').then(m => m.NgoFormComponent) },
    ]
  },

  {
    path: 'legal',
    children: [
      { path: 'faq', loadComponent: () => import('../features/legal/faq/faq.component').then(m => m.FaqComponent) },
      { path: 'terms', loadComponent: () => import('../features/legal/terms/terms.component').then(m => m.TermsComponent) },
    ]
  },

  {
    path: 'dashboard',
    canActivate: [authGuard],
    children: [
      { path: 'donations/:id', loadComponent: () => import('../shared/components/donations-detail/donations-detail.component').then(m => m.DonationsDetailComponent) },
      { path: 'donor', canActivate: [roleGuard(['ADMIN', 'DONOR'])], loadComponent: () => import('../features/dashboard/dashboard-donor/dashboard-donor.component').then(m => m.DashboardDonorComponent) },
      { path: 'ngo', canActivate: [roleGuard(['ADMIN', 'NGO'])], loadComponent: () => import('../features/dashboard/dashboard-ngo/dashboard-ngo.component').then(m => m.DashboardNgoComponent) },
      { path: 'driver', canActivate: [roleGuard(['ADMIN', 'DRIVER'])], loadComponent: () => import('../features/dashboard/dashboard-driver/dashboard-driver.component').then(m => m.DashboardDriverComponent) },
      { path: 'admin', canActivate: [roleGuard(['ADMIN'])], loadComponent: () => import('../features/dashboard/dashboard-admin/dashboard-admin.component').then(m => m.DashboardAdminComponent) },
      { path: 'driver/available-trips', canActivate: [roleGuard(['ADMIN', 'DRIVER'])], loadComponent: () => import('../features/dashboard/components/available-trips/available-trips.component').then(m => m.AvailableTripsComponent) },
      { path: 'trips/:id', canActivate: [roleGuard(['ADMIN', 'DRIVER'])], loadComponent: () => import('../features/dashboard/components/active-trip/active-trip.component').then(m => m.ActiveTripComponent) },
    ]
  },

  {
    path: 'donations',
    children: [
      { path: 'form', canActivate: [roleGuard(['ADMIN', 'DONOR'])], loadComponent: () => import('../features/donations/donation-form/donation-form.component').then(m => m.DonationFormComponent) },
      { path: 'success', loadComponent: () => import('../shared/components/donation-success/donation-success.component').then(m => m.DonationSuccessComponent) },
      { path: 'pending', loadComponent: () => import('../shared/components/donation-pending/donation-pending.component').then(m => m.DonationPendingComponent) },
      { path: 'failure', loadComponent: () => import('../shared/components/donation-failure/donation-failure.component').then(m => m.DonationFailureComponent) },
    ]
  },

  { path: 'ngo/reception/:id', canActivate: [roleGuard(['ADMIN', 'NGO'])], loadComponent: () => import('../features/organizations/ngo-reception/ngo-reception.component').then(m => m.NgoReceptionComponent) },
  { path: 'donate', canActivate: [roleGuard(['ADMIN', 'DONOR'])], loadComponent: () => import('../shared/components/donation-payment/donation-payment.component').then(m => m.DonationPaymentComponent) },
  { path: 'reports', canActivate: [authGuard], loadComponent: () => import('../features/reports/dashboard-stats/dashboard-stats.component').then(m => m.DashboardStatsComponent) },

  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];

