import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {NgoResponseDTO} from '../../../shared/models/ngo.model';
import {FooterComponent} from '../../../shared/components/footer/footer.component';
import {NavbarComponent} from '../../../shared/components/navbar/navbar.component';
import {map} from 'rxjs';
import {AsyncPipe, DatePipe} from '@angular/common';
import {DonationService} from '../../../core/services/donation.service';
import {DonationSummaryResponse} from '../../../shared/models/donation.model';
import {NgoService} from '../../../core/services/ngo.service';
import {ToastrService} from 'ngx-toastr';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-dashboard-ngo',
  imports: [
    FooterComponent,
    NavbarComponent,
    AsyncPipe,
    DatePipe
  ],
  templateUrl: './dashboard-ngo.component.html',
  styleUrl: './dashboard-ngo.component.css'
})
export class DashboardNgoComponent implements OnInit{
  private readonly authService = inject(AuthService);
  private readonly donationService = inject(DonationService);
  private readonly ngoService = inject(NgoService);
  private router = inject(Router);

  private readonly destroyRef = inject(DestroyRef);
  private readonly  toastr = inject(ToastrService);

  ngoProfile: NgoResponseDTO | null = null;
  isLoading = true;
  availableDonations = signal<DonationSummaryResponse[]>([]);
  isLoadingDonations = signal<boolean>(true);

  userName$ = this.authService.currentUser$.pipe(
    map(profile => {
      if(profile && 'ngoName' in profile){
        return profile.ngoName;
      }
      return '';
    })
  );

  ngOnInit(){
    this.loadNgoProfile();
  }

  loadNgoProfile(){
    this.ngoService.getNgoProfile().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (profile) => {
        this.ngoProfile = profile;
        this.isLoading = false;
        if(profile.status === 'APPROVED'){
          this.isLoadingDonations.set(true);
          this.loadAvailableDonations();
        }
      },
      error: (err) => {
        console.error('Error loading NGO profile:', err);
        this.isLoading = false;
        this.toastr.error('No se pudo cargar la información de tu perfil.', 'Error de conexión')
      }
    })
  }

  private loadAvailableDonations(){
    this.donationService.getAvailableDonations().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: data => {
        this.availableDonations.set(data);
        this.isLoadingDonations.set(false);
      },
      error: err => {
        console.error('Error loading available donations:', err);
        this.isLoadingDonations.set(false);
        this.toastr.error('No se pudo cargar la información de las alimentos disponibles en la red.', 'Error al cargar lotes')
      }
    })
  }

}
