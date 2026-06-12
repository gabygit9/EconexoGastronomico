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

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register/role', component: RoleSelectionComponent },
  { path: 'register/donor', component: DonorFormComponent },
  { path: 'register/driver', component: DriverFormComponent },
  { path: 'register/ngo', component: NgoFormComponent },
  { path: 'dashboard/donor', component: DashboardDonorComponent },
  { path: 'dashboard/ngo', component: DashboardNgoComponent },
  { path: 'dashboard/driver', component: DashboardDriverComponent },
  { path: 'dashboard/admin', component: DashboardAdminComponent },
  { path: 'donations/form', component: DonationFormComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];
