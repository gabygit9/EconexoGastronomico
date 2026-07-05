import {Component, DestroyRef, inject, OnInit, signal, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {NgoResponseDTO} from '../../../shared/models/ngo.model';
import {FooterComponent} from '../../../shared/components/footer/footer.component';
import {NavbarComponent} from '../../../shared/components/navbar/navbar.component';
import {map} from 'rxjs';
import {AsyncPipe, DatePipe} from '@angular/common';
import {DonationService} from '../../../core/services/donation.service';
import {DonationRequest, DonationResponse, DonationSummaryResponse} from '../../../shared/models/donation.model';
import {NgoService} from '../../../core/services/ngo.service';
import {ToastrService} from 'ngx-toastr';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {
  DonationConfirmModalComponent
} from '../../../shared/components/donation-confirm-modal/donation-confirm-modal.component';
import {AvailableDonationsComponent} from './available-donations/available-donations.component';
import {DonationListComponent} from '../../../shared/components/donation-list/donation-list.component';

@Component({
  selector: 'app-dashboard-ngo',
  imports: [
    FooterComponent,
    NavbarComponent,
    AsyncPipe,
    AvailableDonationsComponent,
    DonationListComponent,
    DonationConfirmModalComponent
  ],
  templateUrl: './dashboard-ngo.component.html',
  styleUrl: './dashboard-ngo.component.css'
})
export class DashboardNgoComponent implements OnInit{
  private readonly authService = inject(AuthService);
  private readonly ngoService = inject(NgoService);
  private readonly donationService = inject(DonationService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly  toastr = inject(ToastrService);
  private readonly router = inject(Router);

  @ViewChild(AvailableDonationsComponent) availableDonationsComp!: AvailableDonationsComponent;

  ngoProfile: NgoResponseDTO | null = null;
  isLoading = true;

  myDonations = signal<DonationResponse[]>([]);
  isLoadingDonations = signal<boolean>(true);

  selectedDonationForCancel: DonationResponse | null = null;
  showCancelModal = signal(false);

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
    this.loadMyDonations();
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

  loadMyDonations(){
    this.isLoadingDonations.set(true);
    this.donationService.getMyDonations().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (donations) => {
        this.myDonations.set(donations);
        this.isLoadingDonations.set(false);
      },
      error: (err) => {
        this.isLoadingDonations.set(false);
        this.toastr.error('No se pudo cargar el historial de donaciones.', 'Error')
      }
    })
  }

  openDetail(donationId: number) {
    this.router.navigate(['/dashboard/donations', donationId]);
  }

  handleCancelNgoDonation(event: {donationId: number, type: 'CANCEL' | 'REJECT'}){
    if(event.type === 'CANCEL'){
      const donation = this.myDonations().find(d => d.id === event.donationId);
      if(donation) {
        this.openCancelModal(donation);
      }
    }
  }

  openCancelModal(donation: DonationResponse) {
    this.selectedDonationForCancel = donation;
    this.showCancelModal.set(true);
  }

  confirmCancel(){
    if(!this.selectedDonationForCancel) return;

    this.donationService.cancelDonationByNgo(this.selectedDonationForCancel.id).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
        this.toastr.success("Solicitud cancelada")
        this.loadMyDonations();

        if(this.availableDonationsComp){
          this.availableDonationsComp.loadAvailableDonations();
        }

        this.showCancelModal.set(false);
      },
      error: () => this.toastr.error('Error al cancelar la solicitud')
    });
  }

  handleReceiveDonation(donationId: number){
    this.router.navigate(['/ngo/reception', donationId]);
  }
}
