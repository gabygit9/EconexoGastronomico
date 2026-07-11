import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {PaymentService} from '../../../core/services/payment.service';
import {NgoService} from '../../../core/services/ngo.service';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {PaymentRequest} from '../../models/payment.model';
import {NgoResponseDTO} from '../../models/ngo.model';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-donation-payment',
  imports: [
    FormsModule
  ],
  templateUrl: './donation-payment.component.html',
  styleUrl: './donation-payment.component.css'
})
export class DonationPaymentComponent implements OnInit{

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly paymentService = inject(PaymentService);
  private readonly ngoService = inject(NgoService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly toastr = inject(ToastrService);

  ngoList = signal<NgoResponseDTO[]>([]);
  selectedNgoId = signal<number | null>(null);
  amount = signal<number>(0);

  ngOnInit() {
    this.ngoService.getActiveNgos().pipe(takeUntilDestroyed(this.destroyRef)).subscribe(ngos => {
      this.ngoList.set(ngos);
    })

    this.route.queryParams.subscribe(params => {
      if(params['ngoId']){
        this.selectedNgoId.set(Number(params['ngoId']));
      }
    })
  }

  getSelectedNgoName(): string {
    const ngo = this.ngoList().find(ngo => ngo.id === this.selectedNgoId());
    return ngo ? ngo.ngoName : 'la organización';
  }

  onDonate() {
    const request: PaymentRequest = {
      ngoId: this.selectedNgoId(),
      amount: this.amount(),
      description: `Donación a EcoNexo para ${this.getSelectedNgoName()}`
    };

    if(this.amount() <= 0){
      this.toastr.warning('Por favor, ingresa un monto válido');
      return;
    }

    this.paymentService.createPreference(request).subscribe({
      next: (response) => {
        // Redirigimos a Mercado Pago
        window.location.href = response.initPoint;
      },
      error: (er) => {
        this.toastr.error('Hubo un error al iniciar el pago');
      }
    });
  }
}
