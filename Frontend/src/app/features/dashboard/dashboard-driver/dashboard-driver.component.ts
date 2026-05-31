import {Component, inject} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-dashboard-driver',
  imports: [],
  templateUrl: './dashboard-driver.component.html',
  styleUrl: './dashboard-driver.component.css'
})
export class DashboardDriverComponent {
  private router = inject(Router);

  logout(){
    localStorage.removeItem('econexo_token');
    this.router.navigate(['/login']);
  }
}
