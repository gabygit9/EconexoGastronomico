import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {RouterLink} from '@angular/router';
import {LandingService} from '../../core/services/landing.service';
import {LandingStats} from '../../shared/models/stats.model';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {DonationBannerComponent} from '../../shared/components/donation-banner/donation-banner.component';
import {FooterComponent} from '../../shared/components/footer/footer.component';
import {DonationPaymentComponent} from '../../shared/components/donation-payment/donation-payment.component';
import {CurrencyPipe, DecimalPipe} from '@angular/common';

@Component({
  selector: 'app-landing',
  imports: [
    RouterLink,
    DonationBannerComponent,
    FooterComponent,
    DonationPaymentComponent,
    CurrencyPipe,
    DecimalPipe
  ],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent implements OnInit {

  private readonly landingService = inject(LandingService);
  private readonly destroyRef = inject(DestroyRef);

  stats = signal<LandingStats | null>(null);
  isLoadingStats = signal(true);
  showPaymentModal = signal(false);

  ngOnInit() {
    this.landingService.getLandingStats().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (data) => {
        this.stats.set(data);
        this.isLoadingStats.set(false);
      },
      error: (err) => {
        this.isLoadingStats.set(false);
      }
    })
  }
}
