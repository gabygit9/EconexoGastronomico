import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {LogisticsService} from '../../../../core/services/logistics.service';
import {DonationResponse} from '../../../../shared/models/donation.model';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {MapComponent} from '../../../../shared/components/map/map.component';
import {ToastrService} from 'ngx-toastr';
import {GenericModalComponent} from '../../../../shared/components/generic-modal/generic-modal.component';
import {NavbarComponent} from '../../../../shared/components/navbar/navbar.component';
import {FooterComponent} from '../../../../shared/components/footer/footer.component';

@Component({
  selector: 'app-active-trip',
  imports: [
    MapComponent,
    GenericModalComponent,
    NavbarComponent,
    FooterComponent
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
  isUpdatingStatus = signal(false);

  //Modal confirmación
  isModalOpen = signal(false);

  modalConfig = signal({
    title: '',
    message: '',
    confirmText: '',
    confirmButtonClass: '',
  })

  private pendingStatus: 'IN_TRANSIT' | 'DELIVERED' | null = null;

  openConfirmation(action: 'IN_TRANSIT' | 'DELIVERED'){
    this.pendingStatus = action;

    if(action === 'IN_TRANSIT'){
      this.modalConfig.set({
        title: '¿Retiraste la carga?',
        message: 'Estás por notificar que ya tenés la mercadería y vas en camino a la ONG.',
        confirmText: 'Sí, retirar',
        confirmButtonClass: 'bg-[#eb5c0c] hover:bg-[#d4530b]'
      });
    } else {
      this.modalConfig.set({
        title: '¿Entregaste la donación?',
        message: 'Estás por finalizar el viaje y marcar la mercadería como entregada en la ONG.',
        confirmText: 'Sí, entregar',
        confirmButtonClass: 'bg-emerald-600 hover:bg-emerald-700'
      });
    }
    this.isModalOpen.set(true);
  }

  onModalConfirm(){
    this.isModalOpen.set(false);
    if(this.pendingStatus){
      this.executeStatusUpdate(this.pendingStatus);
    }
  }

  onModalCancel(){
    this.isModalOpen.set(false);
    this.pendingStatus = null;
  }

  executeStatusUpdate(newStatus: 'IN_TRANSIT' | 'DELIVERED') {
    const currentTrip = this.trip();
    if(!currentTrip) return;

    this.isUpdatingStatus.set(true);

    this.logisticsService.updateTripStatus(currentTrip.id, newStatus).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
        this.trip.update(t => t ? {...t, status: newStatus } : null);
        this.isUpdatingStatus.set(false);

        this.toastr.success('Estado actualizado correctamente', '¡Excelente!');

        if(newStatus === 'DELIVERED'){
          this.toastr.info('Has completado la entrega. ¡Gracias por tu ayuda!', 'Viaje Finalizado');
          this.router.navigate(['/dashboard/driver']);
        }

      },
      error: (err) => {
        console.error('Error al actualizar el estado', err);
        this.isUpdatingStatus.set(false);
        this.toastr.error('Hubo un problema al actualizar el estado. Intentá nuevamente.', 'Error');
      }
    });
  }

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
        this.router.navigate(['/dashboard/driver']);
      }
    })
  }

}
