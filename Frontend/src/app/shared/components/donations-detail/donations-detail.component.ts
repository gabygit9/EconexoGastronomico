import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {DonationService} from '../../../core/services/donation.service';
import {DonationResponse} from '../../models/donation.model';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {ToastrService} from 'ngx-toastr';
import {NavbarComponent} from '../navbar/navbar.component';
import {AsyncPipe, DatePipe} from '@angular/common';
import {StatusTranslatePipe} from '../../pipes/status-translate.pipe';
import {DriverInfoCardComponent} from '../driver-info-card/driver-info-card.component';
import {FooterComponent} from '../footer/footer.component';
import {AuthService} from '../../../core/services/auth.service';
import {map} from 'rxjs';

@Component({
  selector: 'app-donations-detail',
  imports: [
    NavbarComponent,
    DatePipe,
    StatusTranslatePipe,
    DriverInfoCardComponent,
    FooterComponent,
    AsyncPipe
  ],
  templateUrl: './donations-detail.component.html',
  styleUrl: './donations-detail.component.css'
})
export class DonationsDetailComponent implements OnInit{
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly donationService = inject(DonationService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly toastr = inject(ToastrService);
  private readonly authService = inject(AuthService);

  donation = signal<DonationResponse | null>(null);
  isLoading = signal<boolean>(true);

  userName$ = this.authService.currentUser$.pipe(
    map(profile => {
      if (!profile) return '';
      if ('tradeName' in profile) return profile.tradeName;
      if ('ngoName' in profile) return profile.ngoName;
      return '';
    })
  );

  ngOnInit(){
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.loadDonation(Number(idParam));
    } else {
      this.goBack();
    }
  }

  loadDonation(id: number){
    this.isLoading.set(true);
    this.donationService.getDonationById(id).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (data) => {
        this.donation.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
        this.toastr.error('Donación no encontrada', 'Error')
        this.goBack();
      }
    })
  }

  goBack(){
    window.history.back();
  }
}
