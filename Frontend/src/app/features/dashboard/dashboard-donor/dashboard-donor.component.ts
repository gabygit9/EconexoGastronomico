import {Component, DestroyRef, inject, OnInit} from '@angular/core';
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

@Component({
  selector: 'app-dashboard-donor',
  imports: [
    FooterComponent,
    NavbarComponent,
    AsyncPipe
  ],
  templateUrl: './dashboard-donor.component.html',
  styleUrl: './dashboard-donor.component.css'
})
export class DashboardDonorComponent implements OnInit{
  private readonly authService = inject(AuthService)
  private readonly donorService = inject(DonorService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly  toastr = inject(ToastrService);

  isLoading = true;
  donorProfile: DonorResponse | null = null;

  userName$ = this.authService.currentUser$.pipe(
    map(profile => {
      console.log('Objeto en currentUser$:', profile);
      if(profile && 'tradeName' in profile){
        return profile.tradeName;
      }
      return '';
    })
  );

  ngOnInit(){
    this.loadDonorProfile();
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

  goToNewDonation(){
    this.router.navigate(['/donations/form']);
  }
}
