import {Component, inject} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';

@Component({
  selector: 'app-donation-success',
  imports: [],
  templateUrl: './donation-success.component.html',
  styleUrl: './donation-success.component.css'
})
export class DonationSuccessComponent {
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  goHome() {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard/donor']);
    } else {
      this.router.navigate(['/login']);
    }
  }
}
