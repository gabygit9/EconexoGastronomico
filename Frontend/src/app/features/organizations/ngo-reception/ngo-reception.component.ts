import {Component, DestroyRef, inject, OnInit, signal, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {DonationService} from '../../../core/services/donation.service';
import {DonationItemReception} from '../../../shared/models/donation.model';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {FormsModule} from '@angular/forms';
import {NgxScannerQrcodeComponent} from 'ngx-scanner-qrcode';
import {GenericModalComponent} from '../../../shared/components/generic-modal/generic-modal.component';
import {SignaturePadComponent} from '../../../shared/components/signature-pad/signature-pad.component';

@Component({
  selector: 'app-ngo-reception',
  imports: [
    FormsModule,
    NgxScannerQrcodeComponent,
    GenericModalComponent,
    SignaturePadComponent,
  ],
  templateUrl: './ngo-reception.component.html',
  styleUrl: './ngo-reception.component.css'
})
export class NgoReceptionComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly donationService = inject(DonationService);
  private readonly toastr = inject(ToastrService);
  private readonly destroyRef = inject(DestroyRef);

  donationId = signal<number | null>(null);
  items = signal<(DonationItemReception & { received: boolean })[]>([]);
  isLoading = signal(true);
  comments = signal('');
  isScanning = signal(false);
  isModalOpen = signal(false);
  acceptedDisclaimer = signal(false);
  signatureUrl = signal<string | null>(null);
  isSignatureOpen = signal(false);
  isLegalTermsOpen = signal(false);

  @ViewChild('action') scanner!: NgxScannerQrcodeComponent;
  @ViewChild('signaturePad') signaturePad!: SignaturePadComponent;

  ngOnInit(){
    const id = this.route.snapshot.paramMap.get('id');
    if(id){
      this.donationId.set(+id);
      this.loadItems(+id);
    }
  }

  loadItems(id: number){
    this.donationService.getDonationItems(id).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (data) => {
        const itemsWithReceived = data.map(item => ({ ...item, received: true}));
        this.items.set(itemsWithReceived);
        this.isLoading.set(false);
      },
      error: () => {
        this.toastr.error('Error al cargar los items');
        this.goBack();
      }
    });
  }

  confirmReception(){
    if(!this.donationId()) return;

    if (!this.acceptedDisclaimer()) {
      this.toastr.error('Debes aceptar los términos y condiciones para continuar.');
      return;
    }

    const lostItems = this.items().filter(i => !i.received);

    if(lostItems.length > 0 && this.comments().trim().length < 5){
      this.toastr.warning('Por favor, explica en los comentarios por qué faltan ítems.');
      return;
    }

    if(lostItems.length > 0) {
      this.isModalOpen.set(true);
    } else {
      this.openSignaturePad();
    }
  }

  executeReception(){
    this.isModalOpen.set(false);

    if (!this.signatureUrl()) {
      this.toastr.error('Es necesaria la firma digital para confirmar la recepción.');
      return;
    }

    const itemsToSend = this.items()
      .filter(i => i.received)
      .map(i => ({
        itemId: i.itemId,
        receivedQuantity: i.expectedQuantity
      }));

    const payload = {
      comments: this.comments(),
      receivedItems: itemsToSend,
      acceptedDisclaimer: this.acceptedDisclaimer(),
      signatureUrl: this.signatureUrl()
    }

    this.donationService.receiveDonations(this.donationId()!, payload).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
        this.toastr.success('Donación recibida correctamente');
        this.goBack();
      },
      error: () => {
        this.toastr.error('Error al confirmar recepción');
      }
    })
  }

  goBack(){
    this.router.navigate(['/dashboard/ngo']);
  }

  startScanner(){
    this.isScanning.set(true);
    setTimeout(() => {
      this.scanner.start();
    }, 300);
  }

  handleQrCode(event:any){
    if(event && event.length > 0){
      const scannedId = parseInt(event[0].value);

      if (scannedId === this.donationId()) {
        this.scanner.stop();
        this.isScanning.set(false);
        this.toastr.success('¡Validación exitosa! Donación confirmada.');
      } else {
        this.isScanning.set(false);
        this.toastr.error('Error: El QR no coincide con esta donación.');
      }
    }
  }

  public config: any = {
    constraints: {
      video: {
        facingMode: { ideal: "environment"}
      }
    },
    isBeep: true,
    isDraw: true
  };

  openSignaturePad(){
    this.isSignatureOpen.set(true);
  }

  async saveSignature(){
    const dataUrl = this.signaturePad.toDataURL();
    if(!dataUrl){
      this.toastr.error('La firma está vacía');
      return;
    }

    this.signatureUrl.set(dataUrl);
    this.isSignatureOpen.set(false);
    this.executeReception();
  }

  openLegalTerms(){
    this.isLegalTermsOpen.set(true);
  }

}
