import {Component, DestroyRef, inject, Input, OnInit, signal} from '@angular/core';
import {LogisticsService} from '../../../../core/services/logistics.service';
import {ToastrService} from 'ngx-toastr';
import {DonationResponse} from '../../../../shared/models/donation.model';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {DatePipe} from '@angular/common';
import {DriverResponse} from '../../../../shared/models/driver.model';
import {
  AcceptTripModalComponent
} from '../../../../src/app/features/dashboard/components/accept-trip-modal/accept-trip-modal.component';
import {Router} from '@angular/router';

@Component({
  selector: 'app-available-trips',
  imports: [
    DatePipe,
    AcceptTripModalComponent
  ],
  templateUrl: './available-trips.component.html',
  styleUrl: './available-trips.component.css'
})
export class AvailableTripsComponent implements OnInit {
  // Recibir el perfil para tener acceso a la lista de vehículos
  @Input({ required: true }) driverProfile!: DriverResponse;

  private readonly  logisticsService = inject(LogisticsService);
  private readonly toastr = inject(ToastrService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);

  trips = signal<DonationResponse[]>([]);
  isLoading = signal<boolean>(true);
  locationError = signal<string | null>(null);

  //Estado del modal de aceptación
  selectedTrip = signal<DonationResponse | null>(null);
  isAccepting = signal<boolean>(false);

  ngOnInit(){
    this.getUserLocation();
  }

  /**
   * Get user location
   */
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

  /**
   * Fetch available trips near user location
   * @param latitude User latitude
   * @param longitude User longitude
   */
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
  // ------------- MÉTODOS DEL MODAL ---------------

  openTripDetails(trip: DonationResponse){
    this.selectedTrip.set(trip);
  }

  closeModal(){
    this.selectedTrip.set(null);
  }

  confirmAcceptTrip(vehicleId: number){
    const trip = this.selectedTrip();

    if(!trip || !vehicleId ){
      this.toastr.warning("Debes seleccionar un vehículo válido", "Atención");
      return;
    }

    this.isAccepting.set(true);

    this.logisticsService.acceptTrip(trip.id, vehicleId).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
        this.toastr.success('¡Viaje asignado con éxito!', 'Éxito');
        this.isAccepting.set(false);
        this.closeModal();

        //Actualizar lista de viajes
        this.trips.update(currentTrips => currentTrips.filter(t => t.id !== trip.id));

        this.router.navigate(['/dashboard/trips', trip.id]);
      },
      error: err => {
        const errorMsg = err.error?.message || 'Ocurrió un error al asignar el viaje';
        this.toastr.error(errorMsg, 'Error de Asignación');
        this.isAccepting.set(false);

        //si el viaje ya fue tomado, lo sacamos de la vista
        if(err.status === 409){
          this.trips.update(currentTrips => currentTrips.filter(t => t.id !== trip.id));
          this.closeModal();
        }
      }
    })
  }

}
