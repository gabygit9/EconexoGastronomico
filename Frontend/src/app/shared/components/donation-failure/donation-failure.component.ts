import {Component, inject, OnInit} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-donation-failure',
  imports: [],
  templateUrl: './donation-failure.component.html',
  styleUrl: './donation-failure.component.css'
})
export class DonationFailureComponent {
  private readonly router = inject(Router);

  goHome() { this.router.navigate(['/']); }
}
