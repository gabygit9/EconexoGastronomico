import {Component, inject, OnInit} from '@angular/core';
import {AuthService} from '../../../core/services/auth.service';
import {NavbarComponent} from '../../../shared/components/navbar/navbar.component';
import {FooterComponent} from '../../../shared/components/footer/footer.component';
import {map} from 'rxjs';
import {AsyncPipe} from '@angular/common';
import {DriverResponse} from '../../../shared/models/driver.model';

@Component({
  selector: 'app-dashboard-driver',
  imports: [
    NavbarComponent,
    FooterComponent,
    AsyncPipe
  ],
  templateUrl: './dashboard-driver.component.html',
  styleUrl: './dashboard-driver.component.css'
})
export class DashboardDriverComponent implements OnInit{
  private readonly authService = inject(AuthService);

  driverProfile: DriverResponse | null = null;
  isLoading = true;

  userName$ = this.authService.currentUser$.pipe(
    map(profile => {
      if(profile && 'firstName' && 'lastName' in profile){
        return profile.firstName + ' ' + profile.lastName;
      }
      return '';
    })
  );

  ngOnInit(){

  }

}
