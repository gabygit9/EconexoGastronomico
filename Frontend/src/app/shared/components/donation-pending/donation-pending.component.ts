import {Component, inject, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';

@Component({
  selector: 'app-donation-pending',
  imports: [],
  templateUrl: './donation-pending.component.html',
  styleUrl: './donation-pending.component.css'
})
export class DonationPendingComponent implements OnInit {
  private readonly router = inject(Router);

  ngOnInit() {
    const currentUrl = window.location.href;

    if(currentUrl.includes('ngrok')){
      const params = window.location.search;
      window.location.href = `http://localhost:4200/donations/pending${params}`;
      return;
    }
  }

  goHome() { this.router.navigate(['/']); }
}
