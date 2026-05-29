import {Component, inject, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {NgoResponseDTO} from '../../../shared/models/ngo.model';

@Component({
  selector: 'app-dashboard-ngo',
  imports: [],
  templateUrl: './dashboard-ngo.component.html',
  styleUrl: './dashboard-ngo.component.css'
})
export class DashboardNgoComponent implements OnInit{
  private readonly authService = inject(AuthService);
  private router = inject(Router);

  ngoProfile: NgoResponseDTO | null = null;
  isLoading = true;

  ngOnInit(){
    this.authService.getNgoProfile().subscribe({
      next: (profile) => {
        this.ngoProfile = profile;
        this.isLoading = false;
      },
      error: (error) => {
        console.error(error);
        this.isLoading = false;
      }
    })
  }

  logout(){
    localStorage.removeItem('econexo_token');
    this.router.navigate(['/login']);
  }
}
