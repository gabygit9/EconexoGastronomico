import {Component, inject, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {FooterComponent} from '../../../shared/components/footer/footer.component';
import {NavbarComponent} from '../../../shared/components/navbar/navbar.component';
import {DriverResponse} from '../../../shared/models/driver.model';
import {DonorResponse} from '../../../shared/models/donor.model';

@Component({
  selector: 'app-dashboard-donor',
  imports: [
    FooterComponent,
    NavbarComponent
  ],
  templateUrl: './dashboard-donor.component.html',
  styleUrl: './dashboard-donor.component.css'
})
export class DashboardDonorComponent implements OnInit{
  private readonly authService = inject(AuthService);

  donorProfile: DonorResponse | null = null;
  isLoading = true;

  ngOnInit(){
    this.authService.getDonorProfile().subscribe({
      next: (profile) => {
        this.donorProfile = profile;
        this.isLoading = false;
      },
      error: (error) => {
        console.error(error);
        this.isLoading = false;
      }
    })
  }
}
