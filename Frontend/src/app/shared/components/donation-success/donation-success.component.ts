import {Component, inject, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';

@Component({
  selector: 'app-donation-success',
  imports: [],
  templateUrl: './donation-success.component.html',
  styleUrl: './donation-success.component.css'
})
export class DonationSuccessComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  ngOnInit() {
    const currentUrl = window.location.href;

    if(currentUrl.includes('ngrok')){
      const params = window.location.search;
      window.location.href = `http://localhost:4200/donations/success${params}`;
      return;
    }
  }

  goHome() {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard/donor']);
    } else {
      this.router.navigate(['/login']);
    }
  }
}
