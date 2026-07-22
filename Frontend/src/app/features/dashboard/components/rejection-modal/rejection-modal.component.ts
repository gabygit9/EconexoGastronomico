import {Component, DestroyRef, EventEmitter, inject, Input, Output } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {DonationService} from '../../../../core/services/donation.service';

@Component({
  selector: 'app-rejection-modal',
  imports: [
    FormsModule
  ],
  templateUrl: './rejection-modal.component.html',
  styleUrl: './rejection-modal.component.css'
})
export class RejectionModalComponent {
  private readonly  donationService = inject(DonationService);
  private readonly toastr = inject(ToastrService);
  private readonly destroyRef = inject(DestroyRef);

  @Input() donationId!: number;
  @Output() close = new EventEmitter<boolean>();

  isLoading = false;
  rejectionData = {
    reason: '',
    photoBase64: '',
    date: new Date()
  };
  selectedFileName: string | null = null;

  //Convertir a Base64
  onFileSelected(event:any){
    const file = event.target.files[0];
    if(file){
      this.selectedFileName = file.name;
      const reader = new FileReader();
      reader.onload = () => {
        this.rejectionData.photoBase64 = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  submitRejection(){
    if(!this.rejectionData.reason || this.rejectionData.reason.trim() === '') {
      this.toastr.warning('Por favor, indica el motivo del rechazo.', 'Atención');
      return;
    }

    this.isLoading = true;

    this.donationService.rejectDonationWithDetails(this.donationId, this.rejectionData).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
        this.toastr.success('Rechazo registrado exitosamente');
        this.close.emit(true);
      },
      error: () => {
        this.isLoading = false;
        this.toastr.error('Error al registrar el rechazo');
      }
    });
  }

}
