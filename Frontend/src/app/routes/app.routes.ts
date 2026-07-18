import { Routes } from '@angular/router';
import {LoginComponent} from '../features/auth/login/login.component';
import {RoleSelectionComponent} from '../features/auth/role-selection/role-selection.component';
import {DonorFormComponent} from '../features/auth/donor-form/donor-form.component';
import {DashboardDonorComponent} from '../features/dashboard/dashboard-donor/dashboard-donor.component';
import {NgoFormComponent} from '../features/auth/ngo-form/ngo-form.component';
import {DashboardNgoComponent} from '../features/dashboard/dashboard-ngo/dashboard-ngo.component';
import {DriverFormComponent} from '../features/auth/driver-form/driver-form.component';
import {DashboardDriverComponent} from '../features/dashboard/dashboard-driver/dashboard-driver.component';
import {DonationFormComponent} from '../features/donations/donation-form/donation-form.component';
import {DashboardAdminComponent} from '../features/dashboard/dashboard-admin/dashboard-admin.component';
import {ForgotPasswordComponent} from '../features/auth/login/forgot-password/forgot-password.component';
import {ResetPasswordComponent} from '../features/auth/login/reset-password/reset-password.component';
import {AvailableTripsComponent} from '../features/dashboard/components/available-trips/available-trips.component';
import {ActiveTripComponent} from '../features/dashboard/components/active-trip/active-trip.component';
import {DonationsDetailComponent} from '../shared/components/donations-detail/donations-detail.component';
import {NgoReceptionComponent} from '../features/organizations/ngo-reception/ngo-reception.component';
import {DonationPaymentComponent} from '../shared/components/donation-payment/donation-payment.component';
import {DonationSuccessComponent} from '../shared/components/donation-success/donation-success.component';
import {DonationFailureComponent} from '../shared/components/donation-failure/donation-failure.component';
import {DonationPendingComponent} from '../shared/components/donation-pending/donation-pending.component';
import {DashboardStatsComponent} from '../features/reports/dashboard-stats/dashboard-stats.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'register/role', component: RoleSelectionComponent },
  { path: 'register/donor', component: DonorFormComponent },
  { path: 'register/driver', component: DriverFormComponent },
  { path: 'register/ngo', component: NgoFormComponent },
  { path: 'dashboard/donations/:id', component: DonationsDetailComponent},
  { path: 'dashboard/donor', component: DashboardDonorComponent },
  { path: 'dashboard/ngo', component: DashboardNgoComponent },
  { path: 'dashboard/driver', component: DashboardDriverComponent },
  { path: 'dashboard/admin', component: DashboardAdminComponent },
  { path: 'dashboard/driver/available-trips', component: AvailableTripsComponent },
  { path: 'dashboard/trips/:id', component: ActiveTripComponent },
  { path: 'donations/form', component: DonationFormComponent },
  { path: 'ngo/reception/:id', component: NgoReceptionComponent },
  { path: 'donate', component: DonationPaymentComponent },
  { path: 'donations/success', component: DonationSuccessComponent },
  { path: 'donations/pending', component: DonationPendingComponent },
  { path: 'donations/failure', component: DonationFailureComponent },
  { path: 'reports', component: DashboardStatsComponent },
  { path: 'faq', component: FaqComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];

