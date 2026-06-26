import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
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
import {AvailableTripsComponent} from '../components/available-trips/available-trips.component';
import {DonationResponse} from '../../../shared/models/donation.model';
import {DonationService} from '../../../core/services/donation.service';

@Component({
  selector: 'app-dashboard-driver',
  imports: [
    NavbarComponent,
    FooterComponent,
    AsyncPipe,
    AvailableTripsComponent
  ],
  templateUrl: './dashboard-driver.component.html',
  styleUrl: './dashboard-driver.component.css'
})
export class DashboardDriverComponent implements OnInit{
  private readonly authService = inject(AuthService);
  private readonly driverService = inject(DriverService);
  private readonly donationService = inject(DonationService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly  toastr = inject(ToastrService);

  driverProfile: DriverResponse | null = null;
  isLoading = true;

  activeTrip = signal<DonationResponse | null>(null);

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

        if(profile.status == 'APPROVED'){
          this.checkActiveTrip();
        }
      },
      error: (error) => {
        this.toastr.error('Error al cargar el perfil del conductor', 'Error');
        this.isLoading = false;
      }
    })
  }

  checkActiveTrip(){
    this.donationService.getMyDonations().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (trips) => {
        console.log("Viajes devueltos por el backend: ", trips);
        const current = trips.find((t: DonationResponse) => t.status === 'ASSIGNED' || t.status === 'IN_TRANSIT');
        if(current){
          console.log("¡Se encontró un viaje activo!", current);
          this.activeTrip.set(current);
        }else {
          console.log("No hay viajes activos en la lista");
          this.activeTrip.set(null);
        }
      },
      error: () => console.error("Error loading driver's active trip")
    });
  }

  goToActiveTrip(){
    const trip = this.activeTrip();
    if(trip){
      this.router.navigate(['/dashboard/trips', trip.id]);
    }
  }

}
