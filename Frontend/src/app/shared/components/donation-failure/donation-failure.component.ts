import {Component, inject, OnInit} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-donation-failure',
  imports: [],
  templateUrl: './donation-failure.component.html',
  styleUrl: './donation-failure.component.css'
})
export class DonationFailureComponent implements OnInit {
  private readonly router = inject(Router);

  ngOnInit() {
    const currentUrl = window.location.href;

    if(currentUrl.includes('ngrok')){
      const params = window.location.search;
      window.location.href = `http://localhost:4200/donations/failure${params}`;
      return;
    }
  }

  goHome() { this.router.navigate(['/']); }
}
