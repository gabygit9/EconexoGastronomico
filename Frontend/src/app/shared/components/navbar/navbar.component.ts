import {Component, DestroyRef, inject, Input, OnInit, signal} from '@angular/core';
import {AuthService} from '../../../core/services/auth.service';
import {Router, RouterLink} from '@angular/router';
import {AsyncPipe} from '@angular/common';
import {NotificationService} from '../../../core/services/notification.service';
import {ToastrService} from 'ngx-toastr';
import {interval, map, startWith, switchMap} from 'rxjs';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {NotificationDto} from '../../models/donation.model';
import {DonorResponse} from '../../models/donor.model';
import {NgoResponseDTO} from '../../models/ngo.model';
import {DriverResponse} from '../../models/driver.model';
import {UserAdminResponse} from '../../models/admin.model';

@Component({
  selector: 'app-navbar',
  imports: [AsyncPipe, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit{
  protected authService = inject(AuthService);
  private router = inject(Router);
  private notificationService = inject(NotificationService);
  private toastr = inject(ToastrService);
  private destroyRef = inject(DestroyRef);

  @Input() userName: string = '';
  unreadCount = 0;
  showNotifications = false;
  notifications = signal<NotificationDto[]>([])

  toggleNotifications() {
    this.showNotifications = !this.showNotifications;
    if(this.showNotifications && this.unreadCount > 0){
      this.notificationService.markAllAsRead().subscribe(() => {
        this.unreadCount = 0;
        this.notifications.update(list => list.map(n => ({...n, isRead: true})))
      })
      this.notificationService.getMyNotifications().subscribe({
        next: (data) => this.notifications.set(data),
        error: () => this.toastr.error("Error cargando notificaciones"),
      })
    }
  }

  ngOnInit() {
    this.notificationService.getUnreadCount().subscribe(c => this.unreadCount = c);

    interval(60000)
      .pipe(takeUntilDestroyed(this.destroyRef),
        switchMap(() => this.notificationService.getUnreadCount())
      ).subscribe( count => {
      if(count > this.unreadCount && count > 0){
        this.toastr.warning(`Tenés ${count} nuevas notificaciones`)
      }
      this.unreadCount = count;
    })
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: (err) => {
        console.error('Error al cerrar sesión', err);
        this.router.navigate(['/login']);
      }
    });
  }

  handleLogoClick() {
    const isAuthenticated = this.authService.isAuthenticated();

    if (!isAuthenticated) {
      this.router.navigate(['/login']);
      return;
    }

    const role = this.authService.getUserRole();

    switch(role) {
      case 'ROLE_ADMIN':
        this.router.navigate(['/dashboard/admin']);
        break;
      case 'ROLE_NGO':
        this.router.navigate(['/dashboard/ngo']);
        break;
      case 'ROLE_DONOR':
        this.router.navigate(['/dashboard/donor']);
        break;
      case 'ROLE_DRIVER':
        this.router.navigate(['//dashboard/driver']);
        break;
      default:
        this.router.navigate(['/']);
    }
  }


}
