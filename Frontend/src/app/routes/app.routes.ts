import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register/role', component: RoleSelectionComponent },
  { path: 'register/donor', component: DonorFormComponent },
  //{ path: 'register/driver', component: DriverFormComponent },
  { path: 'register/ngo', component: NgoFormComponent },
  { path: 'dashboard/donor', component: DashboardDonorComponent },
  { path: 'dashboard/ngo', component: DahsboardNgoComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];
