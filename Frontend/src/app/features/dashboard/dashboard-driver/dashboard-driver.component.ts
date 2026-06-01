import {Component, inject, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {NavbarComponent} from '../../../shared/components/navbar/navbar.component';
import {FooterComponent} from '../../../shared/components/footer/footer.component';
import {DriverResponse} from '../../../shared/models/driver.model';

@Component({
  selector: 'app-dashboard-driver',
  imports: [
    NavbarComponent,
    FooterComponent
  ],
  templateUrl: './dashboard-driver.component.html',
  styleUrl: './dashboard-driver.component.css'
})
export class DashboardDriverComponent implements OnInit{
  private readonly authService = inject(AuthService);

  driverProfile: DriverResponse | null = null;
  isLoading = true;

  ngOnInit(){
    this.authService.getDriverProfile().subscribe({
      next: (profile) => {
        this.driverProfile = profile;
        this.isLoading = false;
      },
      error: (error) => {
        console.error(error);
        this.isLoading = false;
      }
    })
  }

}
