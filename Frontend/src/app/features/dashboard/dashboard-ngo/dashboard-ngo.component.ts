import {Component, inject, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {NgoResponseDTO} from '../../../shared/models/ngo.model';
import {FooterComponent} from '../../../shared/components/footer/footer.component';
import {NavbarComponent} from '../../../shared/components/navbar/navbar.component';
import {map} from 'rxjs';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-dashboard-ngo',
  imports: [
    FooterComponent,
    NavbarComponent,
    AsyncPipe
  ],
  templateUrl: './dashboard-ngo.component.html',
  styleUrl: './dashboard-ngo.component.css'
})
export class DashboardNgoComponent implements OnInit{
  private readonly authService = inject(AuthService);
  private router = inject(Router);

  userName$ = this.authService.currentUser$.pipe(
    map(profile => {
      if(profile && 'ngoName' in profile){
        return profile;
      }
      return '';
    })
  );

  ngoProfile: NgoResponseDTO | null = null;
  isLoading = true;

  ngOnInit(){
  }

}
