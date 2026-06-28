import {Component, DestroyRef, inject, Input, OnInit, signal} from '@angular/core';
import {AuthService} from '../../../core/services/auth.service';
import {Router, RouterLink} from '@angular/router';
import {AsyncPipe} from '@angular/common';
import {NotificationService} from '../../../core/services/notification.service';
import {ToastrService} from 'ngx-toastr';
import {interval, switchMap} from 'rxjs';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {NotificationDto} from '../../models/donation.model';

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
}
