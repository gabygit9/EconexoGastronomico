import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {AuthService} from '../../../core/services/auth.service';
import {StatsService} from '../../../core/services/reports/stats.service';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {NgoStatsComponent} from '../ngo-stats/ngo-stats.component';
import {DonorStatsComponent} from '../donor-stats/donor-stats.component';
import {DriverStatsComponent} from '../driver-stats/driver-stats.component';
import {AdminStatsComponent} from '../admin-stats/admin-stats.component';
import {AsyncPipe} from '@angular/common';
import {NavbarComponent} from '../../../shared/components/navbar/navbar.component';
import {FooterComponent} from '../../../shared/components/footer/footer.component';
import {map, startWith} from 'rxjs';
import {DonorResponse} from '../../../shared/models/donor.model';
import {NgoResponseDTO} from '../../../shared/models/ngo.model';
import {DriverResponse} from '../../../shared/models/driver.model';
import {UserAdminResponse} from '../../../shared/models/admin.model';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-dashboard-stats',
  imports: [
    NgoStatsComponent,
    DonorStatsComponent,
    DriverStatsComponent,
    AdminStatsComponent,
    AsyncPipe,
    FormsModule,
    NavbarComponent,
    FooterComponent
  ],
  templateUrl: './dashboard-stats.component.html',
  styleUrl: './dashboard-stats.component.css'
})
export class DashboardStatsComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly statsService = inject(StatsService);
  private readonly toastr = inject(ToastrService);
  private readonly destroyRef = inject(DestroyRef);

  stats: any;
  userRole: string | null = null;
  loading = true;

  startDateInput: string | null = null;
  endDateInput: string | null = null;
  private appliedStartDate: string | null = null;
  private appliedEndDate: string | null = null;

  userName$ = this.authService.currentUser$.pipe(
    map(profile => {
      if(profile) {
        if(this.isDonor(profile)) {
          return profile.tradeName;
        } else if(this.isNgo(profile)){
          return profile.ngoName;
        } else if(this.isDriver(profile)){
          return profile.firstName + ' ' + profile.lastName;
        } else if(this.isAdmin(profile)){
          return this.decodeTokenName();
        }
      }

      return this.decodeTokenName();
    }),
    startWith(this.decodeTokenName())
  );

  ngOnInit() {
    this.userRole = this.authService.getUserRole();
    this.loadStats();
  }

  loadStats(){
    this.loading = true;
    this.statsService.getStats(this.appliedStartDate ?? undefined, this.appliedEndDate ?? undefined)
      .pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
      },
      error: () => {
        this.toastr.error('Error al cargar las estadísticas');
        this.loading = false;
      }
    })
  }

  applyDateFilter(){
    this.appliedStartDate = this.startDateInput;
    this.appliedEndDate = this.endDateInput;
    this.loadStats();
  }

  clearDateFilter(){
    this.startDateInput = null;
    this.endDateInput = null;
    this.appliedStartDate = null;
    this.appliedEndDate = null;
    this.loadStats();
  }

  private isDonor(profile:any): profile is DonorResponse{
    return 'tradeName' in profile;
  }

  private isNgo(profile:any): profile is NgoResponseDTO{
    return 'ngoName' in profile;
  }

  private isDriver(profile:any): profile is DriverResponse{
    return 'firstName' in profile && 'lastName' in profile;
  }

  private isAdmin(profile:any): profile is UserAdminResponse{
    return 'email' in profile;
  }

  private decodeTokenName(): string {
    const token = localStorage.getItem('econexo_token');
    if (!token) return 'Administrador';

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub || 'Administrador';
    } catch (e) {
      return 'Administrador';
    }
  }
}
