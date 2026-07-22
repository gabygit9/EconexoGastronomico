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
import {AuthService} from '../../../../core/services/auth.service';
import {map} from 'rxjs';
import {AsyncPipe} from '@angular/common';
import {DeliveryEvidenceModalComponent} from '../delivery-evidence-modal/delivery-evidence-modal.component';
import { QRCodeComponent } from 'angularx-qrcode';
import {RejectionModalComponent} from '../rejection-modal/rejection-modal.component';

@Component({
  selector: 'app-active-trip',
  imports: [
    MapComponent,
    GenericModalComponent,
    NavbarComponent,
    FooterComponent,
    AsyncPipe,
    DeliveryEvidenceModalComponent,
    QRCodeComponent,
    RejectionModalComponent
  ],
  templateUrl: './active-trip.component.html',
  styleUrl: './active-trip.component.css'
})
export class ActiveTripComponent implements OnInit {

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly logisticsService = inject(LogisticsService);
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly toastr = inject(ToastrService);

  trip = signal<DonationResponse | null>(null);
  isLoading = signal(true);
  isUpdatingStatus = signal(false);
  showEvidenceModal = signal(false);

  showQr = signal(false);

  //Modal rechazo
  showRejectModal = signal(false);

  //Modal cancelación
  showCancelModal = signal(false);
  isCanceling = signal(false);

  //Modal confirmación
  isModalOpen = signal(false);

  modalConfig = signal({
    title: '',
    message: '',
    confirmText: '',
    confirmButtonClass: '',
  })

  private pendingStatus: 'IN_TRANSIT' | 'DELIVERED_PENDING_NGO' | null = null;

  userName$ = this.authService.currentUser$.pipe(
    map(profile => {
      if(profile && 'firstName' in profile && 'lastName' in profile){
        return profile.firstName + ' ' + profile.lastName;
      }
      return '';
    })
  );

  openConfirmation(action: 'IN_TRANSIT' | 'DELIVERED_PENDING_NGO'){
    this.pendingStatus = action;

    if(action === 'IN_TRANSIT'){
      this.showEvidenceModal.set(true);
    } else {
      this.modalConfig.set({
        title: '¿Entregaste la donación?',
        message: 'Estás por finalizar el viaje y marcar la mercadería como entregada en la ONG.',
        confirmText: 'Sí, entregar',
        confirmButtonClass: 'bg-emerald-600 hover:bg-emerald-700'
      });
      this.isModalOpen.set(true);
    }
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

  openCancelModal(){
    this.showCancelModal.set(true);
  }

  closeCancelModal(){
    this.showCancelModal.set(false);
  }

  openRejectModal(){
    this.showRejectModal.set(true);
  }

  closeRejectModal(){
    this.showRejectModal.set(false);
  }

  executeStatusUpdate(newStatus: 'IN_TRANSIT' | 'DELIVERED_PENDING_NGO') {
    const currentTrip = this.trip();
    if(!currentTrip) return;

    this.isUpdatingStatus.set(true);

    this.logisticsService.updateTripStatus(currentTrip.id, newStatus).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
        this.trip.update(t => t ? {...t, status: newStatus } : null);
        this.isUpdatingStatus.set(false);

        this.toastr.success('Estado actualizado correctamente', '¡Excelente!');

        if(newStatus === 'DELIVERED_PENDING_NGO'){
          this.toastr.info('Has completado la entrega. ¡Gracias por tu ayuda!', 'Viaje Finalizado');
          this.goBack();
        }

      },
      error: (err) => {
        this.isUpdatingStatus.set(false);
        this.toastr.error('Hubo un problema al actualizar el estado. Intentá nuevamente.', 'Error');
      }
    });
  }

  ngOnInit() {
    const tripId = this.route.snapshot.paramMap.get('id');
    if(tripId){
      this.loadTripDetails(+tripId);
    } else {
      this.goBack();
    }
  }

  goBack() {
    this.router.navigate(['/dashboard/driver']);
  }

  private loadTripDetails(id:number){
    this.isLoading.set(true);
    this.logisticsService.getTripById(id).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (data) => {
        this.trip.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
        this.toastr.error('El viaje no existe o no está disponible.', 'Error');
        this.goBack();
      }
    })
  }

  confirmCancelTrip(){
    const currentTrip = this.trip();
    if(!currentTrip) return;

    this.isCanceling.set(true);
    this.logisticsService.cancelTrip(currentTrip.id).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
        this.isCanceling.set(false);
        this.closeCancelModal();
        this.toastr.success('Viaje cancelado. La donación fue liberada para la red.', 'Viaje Liberado');
        this.goBack();
      },
      error: (err) => {
        this.isCanceling.set(false);
        this.toastr.error('Hubo un problema al cancelar el viaje. Intentá nuevamente.', 'Error');
      }
    })
  }

  onEvidenceModalClose(success: boolean){
    this.showEvidenceModal.set(false);
    if(success){
      const currentTrip = this.trip();
      if(currentTrip){
        this.loadTripDetails(currentTrip.id);
      }
    }
  }

  generateQrAndNotifyArrival(){
    const currentTrip = this.trip();
    if(!currentTrip) return;

    this.isUpdatingStatus.set(true);

    this.logisticsService.updateTripStatus(currentTrip.id, 'DELIVERED_PENDING_NGO').pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
        this.isUpdatingStatus.set(false);
        this.showQr.set(true);
        this.toastr.success('Has notificado tu llegada.', 'Mostrá este código a la ONG');
      },
      error: () => {
        this.isUpdatingStatus.set(false);
        this.toastr.error('Error al notificar tu llegada.')
      }
    })
  }

  onRejectionModalClose(success: boolean) {
    this.showRejectModal.set(false); // Cierra el modal
    if (success) {
      this.goBack();
    }
  }

}
