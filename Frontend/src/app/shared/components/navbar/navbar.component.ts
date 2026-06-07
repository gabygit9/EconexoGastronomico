import {Component, inject, Input} from '@angular/core';
import {AuthService} from '../../../core/services/auth.service';
import {Router, RouterLink} from '@angular/router';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-navbar',
  imports: [AsyncPipe, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  protected authService = inject(AuthService);
  private router = inject(Router);

  @Input() userName: string = '';

  logout() {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: (err) => {
        console.error('Error al cerrar sesión', err);
        this.router.navigate(['/login']);
      }
    });
  }
}
