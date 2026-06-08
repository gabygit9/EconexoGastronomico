import {Component, EventEmitter, Input, Output} from '@angular/core';
import {DonationSummaryResponse} from '../../models/donation.model';

@Component({
  selector: 'app-donation-confirm-modal',
  imports: [],
  templateUrl: './donation-confirm-modal.component.html',
  styleUrl: './donation-confirm-modal.component.css'
})
export class DonationConfirmModalComponent {

  @Input({ required: true }) donation!: DonationSummaryResponse;
  @Output() confirm = new EventEmitter<number>();
  @Output() close = new EventEmitter<void>();

  onClose(){
    this.close.emit();
  }

  onConfirm(){
    this.confirm.emit(this.donation.id);
  }
}
