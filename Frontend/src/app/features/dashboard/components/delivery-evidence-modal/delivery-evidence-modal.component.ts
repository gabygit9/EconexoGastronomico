import {Component, DestroyRef, EventEmitter, inject, Input, Output, signal, ViewChild} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {DeliveryEvidence} from '../../../../shared/models/donation.model';
import {LogisticsService} from '../../../../core/services/logistics.service';
import {ToastrService} from 'ngx-toastr';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {SignaturePadComponent} from '../../../../shared/components/signature-pad/signature-pad.component';
import {GenericModalComponent} from '../../../../shared/components/generic-modal/generic-modal.component';

@Component({
  selector: 'app-delivery-evidence-modal',
  imports: [
    FormsModule,
    SignaturePadComponent,
    GenericModalComponent
  ],
  templateUrl: './delivery-evidence-modal.component.html',
  styleUrl: './delivery-evidence-modal.component.css'
})
export class DeliveryEvidenceModalComponent {
  private readonly  logisticsService = inject(LogisticsService);
  private readonly toastr = inject(ToastrService);
  private readonly destroyRef = inject(DestroyRef);

  acceptedDisclaimer = signal(false);
  isTermsOpen = false;
  selectedFileName: string | null = null;

  @Input() tripId!: number;
  @Output() close = new EventEmitter<boolean>();
  @ViewChild(SignaturePadComponent) signaturePad!: SignaturePadComponent;

  isLoading = false;
  evidence: DeliveryEvidence = {
    temperature: 0,
    evidencePhotoUrl: '',
    driverSignatureUrl: ''
  };

  openTerms() {
    this.isTermsOpen = true;
  }

  //Convertir a Base64
  onFileSelected(event:any){
    const file = event.target.files[0];
    if(file){
      this.selectedFileName = file.name;
      const reader = new FileReader();
      reader.onload = () => {
        this.evidence.evidencePhotoUrl = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  submitEvidence(){
    const signature = this.signaturePad.toDataURL();
    if(!signature || signature === 'data:,') {
      this.toastr.warning('Por favor, firma antes de continuar.');
      return;
    }
    if(!this.evidence.evidencePhotoUrl){
      this.toastr.warning('Por favor, seleccioná una foto de evidencia.');
      return;
    }

    this.evidence.driverSignatureUrl = signature;
    this.isLoading = true;

    this.logisticsService.registerDriverDelivery(this.tripId, this.evidence).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
        this.toastr.success('Evidencia registrada exitosamente');
        this.close.emit(true);
      },
      error: (err) => {
        this.isLoading = false;
        console.error("Error al registrar:", err);
        this.toastr.error('Error al registrar evidencia');
      }
    });
  }

  clearSignature(){
    this.signaturePad.clear();
  }


}
