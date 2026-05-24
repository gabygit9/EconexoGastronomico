import {Component, inject} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-dashboard-donor',
  imports: [],
  templateUrl: './dashboard-donor.component.html',
  styleUrl: './dashboard-donor.component.css'
})
export class DashboardDonorComponent {
  private router = inject(Router);

  logout(){
    localStorage.removeItem('econexo_token');
    this.router.navigate(['/login']);
  }
}
