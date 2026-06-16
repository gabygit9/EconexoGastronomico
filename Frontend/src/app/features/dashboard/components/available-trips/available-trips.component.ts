import {ChangeDetectorRef, Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {LogisticsService} from '../../../../core/services/logistics.service';
import {ToastrService} from 'ngx-toastr';
import {DonationResponse} from '../../../../shared/models/donation.model';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-available-trips',
  imports: [
    DatePipe
  ],
  templateUrl: './available-trips.component.html',
  styleUrl: './available-trips.component.css'
})
export class AvailableTripsComponent implements OnInit {
  private readonly  logisticsService = inject(LogisticsService);
  private readonly toastr = inject(ToastrService);
  private readonly destroyRef = inject(DestroyRef);

  trips = signal<DonationResponse[]>([]);
  isLoading = signal<boolean>(true);
  locationError = signal<string | null>(null);

  ngOnInit(){
    this.getUserLocation();
  }

  private getUserLocation(){
   if(!navigator.geolocation){
     this.locationError.set("Your browser doesn't support geolocalizatión");
     this.isLoading.set(false);
     return;
   }
   navigator.geolocation.getCurrentPosition(
     position => {
       const lat = position.coords.latitude;
       const lng = position.coords.longitude;
       this.fetchTrips(lat, lng);
   },
     error => {
       this.handleLocationError(error);
       this.isLoading.set(false);
     },
     {
       enableHighAccuracy: true,
       timeout: 10000,
       maximumAge: 0
     }
   );
  }

  private fetchTrips(latitude: number, longitude: number){
    this.logisticsService.getAvailableTripsNearby(latitude, longitude).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: data => {
        this.trips.set(data);
        this.isLoading.set(false);
      },
      error: err => {
        this.toastr.error('Error al cargar los viajes disponibles', 'Error');
        this.isLoading.set(false);
      }
    });
  }

  private handleLocationError(error: GeolocationPositionError){
   switch (error.code) {
     case error.PERMISSION_DENIED:
       this.locationError.set("Por favor, permite el acceso a tu ubicación para ver viajes cercanos.");
       break;
     case error.POSITION_UNAVAILABLE:
       this.locationError.set("La información de ubicación no está disponible en este momento.");
       break;
     case error.TIMEOUT:
       this.locationError.set("Se agotó el tiempo de espera para obtener la ubicación.");
       break;
     default:
       this.locationError.set("Ocurrió un error al obtener la ubicación.");
       break;
   }
  }

}
