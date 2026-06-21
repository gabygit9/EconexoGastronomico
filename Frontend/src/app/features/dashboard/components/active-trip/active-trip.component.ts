import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {LogisticsService} from '../../../../core/services/logistics.service';
import {DonationResponse} from '../../../../shared/models/donation.model';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {MapComponent} from '../../../../shared/components/map/map.component';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-active-trip',
  imports: [
    MapComponent
  ],
  templateUrl: './active-trip.component.html',
  styleUrl: './active-trip.component.css'
})
export class ActiveTripComponent implements OnInit {

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly logisticsService = inject(LogisticsService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly toastr = inject(ToastrService);

  trip = signal<DonationResponse | null>(null);
  isLoading = signal(true);

  ngOnInit() {
    const tripId = this.route.snapshot.paramMap.get('id');
    if(tripId){
      this.loadTripDetails(+tripId);
    }
  }

  private loadTripDetails(id:number){
    this.logisticsService.getTripById(id).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (data) => {
        this.trip.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
        this.toastr.error('El viaje no existe o no está disponible.', 'Error');
        this.router.navigate(['/dashboard/driver/available-trips']);
      }
    })
  }

}
