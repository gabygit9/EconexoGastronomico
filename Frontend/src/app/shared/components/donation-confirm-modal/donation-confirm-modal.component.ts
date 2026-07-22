import {Component, EventEmitter, Input, Output} from '@angular/core';
import {DonationResponse, DonationSummaryResponse} from '../../models/donation.model';

@Component({
  selector: 'app-donation-confirm-modal',
  imports: [],
  templateUrl: './donation-confirm-modal.component.html',
  styleUrl: './donation-confirm-modal.component.css'
})
export class DonationConfirmModalComponent {

  @Input() title: string = 'Confirmar Acción';
  @Input() message: string = '¿Estás seguro de realizar esta acción?';
  @Input() confirmButtonText: string = 'Confirmar';
  @Input() infoMessage: string | null =
    'Al confirmar, el sistema de EcoNexo asignará automáticamente un Conductor Voluntario para retirar este lote completo y llevarlo a tu organización.';


  @Input({ required: true }) donation!: DonationResponse | DonationSummaryResponse;
  @Output() confirm = new EventEmitter<number>();
  @Output() close = new EventEmitter<void>();

  onClose(){
    this.close.emit();
  }

  onConfirm(){
    this.confirm.emit(this.donation.id);
  }

  isSummary(donation: DonationResponse | DonationSummaryResponse): donation is DonationSummaryResponse {
    return (donation as DonationSummaryResponse).requiresRefrigeration !== undefined;
  }
}
