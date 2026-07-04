import {Component, DestroyRef, inject, OnInit, signal, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {DonationService} from '../../../core/services/donation.service';
import {DonationItemReception} from '../../../shared/models/donation.model';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {FormsModule} from '@angular/forms';
import {NgxScannerQrcodeComponent} from 'ngx-scanner-qrcode';

@Component({
  selector: 'app-ngo-reception',
  imports: [
    FormsModule,
    NgxScannerQrcodeComponent,
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
  items = signal<DonationItemReception[]>([]);
  isLoading = signal<boolean>(true);
  comments = signal('');
  isScanning = signal<boolean>(false);

  @ViewChild('action') scanner!: NgxScannerQrcodeComponent;

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
        this.items.set(data);
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

    this.donationService.receiveDonations(this.donationId()!, { comments: this.comments() }).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
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
        this.donationId.set(scannedId);
        this.confirmReception();
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

}
