import {Component, inject, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {FooterComponent} from '../../../shared/components/footer/footer.component';
import {NavbarComponent} from '../../../shared/components/navbar/navbar.component';
import {DriverResponse} from '../../../shared/models/driver.model';
import {DonorResponse} from '../../../shared/models/donor.model';
import {map} from 'rxjs';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-dashboard-donor',
  imports: [
    FooterComponent,
    NavbarComponent,
    AsyncPipe
  ],
  templateUrl: './dashboard-donor.component.html',
  styleUrl: './dashboard-donor.component.css'
})
export class DashboardDonorComponent implements OnInit{
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  userName$ = this.authService.currentUser$.pipe(
    map(profile => {
      if(profile && 'tradeName' in profile){
        return profile;
      }
      return '';
    })
  );

  ngOnInit(){

  }

  goToNewDonation(){
    this.router.navigate(['/donations/form']);
  }
}
