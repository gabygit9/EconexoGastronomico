import {Component, inject, OnInit} from '@angular/core';
import {NavbarComponent} from '../../../shared/components/navbar/navbar.component';
import {FooterComponent} from '../../../shared/components/footer/footer.component';
import {AuthService} from '../../../core/services/auth.service';
import {map, startWith} from 'rxjs';
import {DonorResponse} from '../../../shared/models/donor.model';
import {NgoResponseDTO} from '../../../shared/models/ngo.model';
import {DriverResponse} from '../../../shared/models/driver.model';
import {UserAdminResponse} from '../../../shared/models/admin.model';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-faq',
  imports: [
    NavbarComponent,
    FooterComponent,
    AsyncPipe
  ],
  templateUrl: './faq.component.html',
  styleUrl: './faq.component.css'
})
export class FaqComponent implements OnInit{
  protected authService = inject(AuthService);
  userRole: string | null = null;

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
  }

  private isDonor(profile:any): profile is DonorResponse{ return 'tradeName' in profile; }
  private isNgo(profile:any): profile is NgoResponseDTO{ return 'ngoName' in profile; }
  private isDriver(profile:any): profile is DriverResponse{ return 'firstName' in profile && 'lastName' in profile; }
  private isAdmin(profile:any): profile is UserAdminResponse{ return 'email' in profile; }

  private decodeTokenName(): string {
    const token = localStorage.getItem('econexo_token');
    if (!token) return 'Usuario';

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub || 'Usuario';
    } catch (e) {
      return 'Usuario';
    }
  }

}
