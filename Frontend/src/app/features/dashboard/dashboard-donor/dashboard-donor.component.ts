import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {FooterComponent} from '../../../shared/components/footer/footer.component';
import {NavbarComponent} from '../../../shared/components/navbar/navbar.component';
import {DriverResponse} from '../../../shared/models/driver.model';
import {DonorResponse} from '../../../shared/models/donor.model';
import {map} from 'rxjs';
import {AsyncPipe} from '@angular/common';
import {DonorService} from '../../../core/services/donor.service';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {ToastrService} from 'ngx-toastr';
import {DonationListComponent} from '../../../shared/components/donation-list/donation-list.component';
import {DonationService} from '../../../core/services/donation.service';
import {DonationResponse} from '../../../shared/models/donation.model';

@Component({
  selector: 'app-dashboard-donor',
  imports: [
    FooterComponent,
    NavbarComponent,
    AsyncPipe,
    DonationListComponent
  ],
  templateUrl: './dashboard-donor.component.html',
  styleUrl: './dashboard-donor.component.css'
})
export class DashboardDonorComponent implements OnInit{
  private readonly authService = inject(AuthService)
  private readonly donorService = inject(DonorService);
  private readonly donationService = inject(DonationService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly  toastr = inject(ToastrService);

  isLoading = true;
  donorProfile: DonorResponse | null = null;

  myDonations = signal<DonationResponse[]>([]);
  isLoadingDonations = signal<boolean>(true);

  userName$ = this.authService.currentUser$.pipe(
    map(profile => {
      if(profile && 'tradeName' in profile){
        return profile.tradeName;
      }
      return '';
    })
  );

  ngOnInit(){
    this.loadDonorProfile();
    this.loadMyDonations();
  }

  loadDonorProfile(){
    this.donorService.getDonorProfile().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (profile) => {
        this.isLoading = false;
        this.donorProfile = profile;
      },
      error: (error) => {
        this.toastr.error('Error al cargar el perfil del donante', 'Error');
        this.isLoading = false;
      }
    })
  }

  loadMyDonations(){
    this.isLoadingDonations.set(true);
    this.donationService.getMyDonations().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (donations) => {
        this.myDonations.set(donations);
        this.isLoadingDonations.set(false);
      },
      error: (error) => {
        this.toastr.error('Error al cargar el historial de donaciones', 'Error');
        this.isLoadingDonations.set(false);
      }
    })
  }

  goToNewDonation(){
    this.router.navigate(['/donations/form']);
  }

  openDetail(donationId: number) {
    this.router.navigate(['/dashboard/donations', donationId]);
  }
}
