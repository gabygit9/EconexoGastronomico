import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {AuthService} from '../../../core/services/auth.service';
import {NavbarComponent} from '../../../shared/components/navbar/navbar.component';
import {FooterComponent} from '../../../shared/components/footer/footer.component';
import {map} from 'rxjs';
import {AsyncPipe} from '@angular/common';
import {DriverResponse} from '../../../shared/models/driver.model';
import {DriverService} from '../../../core/services/driver.service';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

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
  private readonly driverService = inject(DriverService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly  toastr = inject(ToastrService);

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
    this.loadDonorProfile();
  }

  loadDonorProfile() {
    this.driverService.getDriverProfile().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (profile) => {
        this.isLoading = false;
        this.driverProfile = profile;
      },
      error: (error) => {
        this.toastr.error('Error al cargar el perfil del conductor', 'Error');
        this.isLoading = false;
      }
    })
  }

}
