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
import {
  DonationConfirmModalComponent
} from '../../../shared/components/donation-confirm-modal/donation-confirm-modal.component';
import {AvailableDonationsComponent} from './available-donations/available-donations.component';

@Component({
  selector: 'app-dashboard-ngo',
  imports: [
    FooterComponent,
    NavbarComponent,
    AsyncPipe,
    DatePipe,
    DonationConfirmModalComponent,
    AvailableDonationsComponent
  ],
  templateUrl: './dashboard-ngo.component.html',
  styleUrl: './dashboard-ngo.component.css'
})
export class DashboardNgoComponent implements OnInit{
  private readonly authService = inject(AuthService);
  private readonly ngoService = inject(NgoService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly  toastr = inject(ToastrService);

  ngoProfile: NgoResponseDTO | null = null;
  isLoading = true;

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
      },
      error: (err) => {
        console.error('Error loading NGO profile:', err);
        this.isLoading = false;
        this.toastr.error('No se pudo cargar la información de tu perfil.', 'Error de conexión')
      }
    })
  }

}
