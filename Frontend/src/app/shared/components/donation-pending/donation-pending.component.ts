import {Component, inject} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';

@Component({
  selector: 'app-donation-pending',
  imports: [],
  templateUrl: './donation-pending.component.html',
  styleUrl: './donation-pending.component.css'
})
export class DonationPendingComponent {
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  goHome() { this.router.navigate(['/']); }
}
