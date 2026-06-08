import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {DonationService} from '../../../../core/services/donation.service';
import {ToastrService} from 'ngx-toastr';
import {DonationSummaryResponse} from '../../../../shared/models/donation.model';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {
  DonationConfirmModalComponent
} from '../../../../shared/components/donation-confirm-modal/donation-confirm-modal.component';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-available-donations',
  imports: [
    DonationConfirmModalComponent,
    DatePipe
  ],
  templateUrl: './available-donations.component.html',
  styleUrl: './available-donations.component.css'
})
export class AvailableDonationsComponent implements OnInit {
  private readonly donationService = inject(DonationService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly toastr = inject(ToastrService);

  availableDonations = signal<DonationSummaryResponse[]>([]);
  isLoadingDonations = signal<boolean>(true);
  selectedDonation = signal<DonationSummaryResponse | null>(null);

  ngOnInit() {
    this.loadAvailableDonations();
  }

  private loadAvailableDonations() {
    this.donationService.getAvailableDonations()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: data => {
          this.availableDonations.set(data);
          this.isLoadingDonations.set(false);
        },
        error: err => {
          console.error('Error loading available donations:', err);
          this.isLoadingDonations.set(false);
          this.toastr.error('No se pudo cargar la información de los alimentos disponibles en la red.', 'Error al cargar lotes');
        }
      });
  }

  openModal(donation: DonationSummaryResponse) {
    this.selectedDonation.set(donation);
  }

  closeModal() {
    this.selectedDonation.set(null);
  }

  confirmDonationRequest(donationId: number) {
    this.donationService.requestDonation(donationId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.closeModal();
          this.toastr.success('Un conductor voluntario pasará a retirarlo pronto.', '¡Lote solicitado con éxito!');
          this.availableDonations.update(currentDonations =>
            currentDonations.filter(donation => donation.id !== donationId)
          );
        },
        error: (err) => {
          console.error('Error al solicitar la donación:', err);
          this.closeModal();
          this.toastr.error('No se pudo procesar la solicitud. Es posible que el lote ya no esté disponible.', 'Error al solicitar.');
          this.loadAvailableDonations();
        }
      });
  }
}
