import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
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
import {map} from 'rxjs';
import {DonorResponse} from '../../../shared/models/donor.model';
import {NgoResponseDTO} from '../../../shared/models/ngo.model';
import {DriverResponse} from '../../../shared/models/driver.model';
import {UserAdminResponse} from '../../../shared/models/admin.model';

@Component({
  selector: 'app-dashboard-stats',
  imports: [
    NgoStatsComponent,
    DonorStatsComponent,
    DriverStatsComponent,
    AdminStatsComponent,
    AsyncPipe,
    NavbarComponent,
    FooterComponent
  ],
  templateUrl: './dashboard-stats.component.html',
  styleUrl: './dashboard-stats.component.css'
})
export class DashboardStatsComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly statsService = inject(StatsService);
  private readonly toastr = inject(ToastrService);
  private readonly destroyRef = inject(DestroyRef);

  stats: any;
  userRole: string | null = null;
  loading = true;

  userName$ = this.authService.currentUser$.pipe(
    map(profile => {
      if(!profile) return '';

      if(this.isDonor(profile)) {
        return profile.tradeName;
      } else if (this.isAdmin(profile)) {
        return profile.email;
      } else if(this.isDriver(profile)){
        return profile.firstName + ' ' + profile.lastName;
      } else if(this.isNgo(profile)){
        return profile.ngoName;
      }
      return '';
    })
  );

  ngOnInit() {
    this.userRole = this.authService.getUserRole();

    this.statsService.getStats().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
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
}
