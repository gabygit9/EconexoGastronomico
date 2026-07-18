import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {AdminService} from '../../../core/services/admin.service';
import {UserAdminResponse} from '../../../shared/models/admin.model';
import {BehaviorSubject, filter, map, startWith} from 'rxjs';
import {Status} from '../../../shared/models/login.model';
import {AsyncPipe, DatePipe, NgClass} from '@angular/common';
import {ToastrService} from 'ngx-toastr';
import {AuthService} from '../../../core/services/auth.service';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {NavbarComponent} from '../../../shared/components/navbar/navbar.component';
import {FooterComponent} from '../../../shared/components/footer/footer.component';
import {StatusTranslatePipe} from '../../../shared/pipes/status-translate.pipe';
import {RoleTranslatePipe} from '../../../shared/pipes/role-translate.pipe';
import {Router} from '@angular/router';

@Component({
  selector: 'app-dashboard-admin',
  imports: [
    NgClass,
    DatePipe,
    AsyncPipe,
    NavbarComponent,
    FooterComponent,
    StatusTranslatePipe,
    RoleTranslatePipe
  ],
  templateUrl: './dashboard-admin.component.html',
  styleUrl: './dashboard-admin.component.css'
})
export class DashboardAdminComponent implements OnInit {

  private readonly authService = inject(AuthService);
  private readonly adminService = inject(AdminService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly toastr = inject(ToastrService);
  private readonly router = inject(Router);

  users$ = new BehaviorSubject<UserAdminResponse[]>([]);
  isLoading = true;
  updatingUserId: number | null = null;

  userName$ = this.authService.currentUser$.pipe(
    map(profile => (profile as any)?.name || this.decodeTokenName()),
    startWith(this.decodeTokenName())
  );

  private decodeTokenName(): string {
    const token = localStorage.getItem('econexo_token');
    if (!token) return 'Administrador';

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub || 'Administrador';
    } catch (e) {
      return 'Administrador';
    }
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(){
    this.adminService.getAllUsers().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (data) => {
        this.users$.next(data);
        this.isLoading = false;
      },
      error: (error) => {
        this.toastr.error('Error al cargar usuarios', 'Error del Servidor');
        this.isLoading = false;
        console.error(error);
      }
    });
  }

  changeStatus(userId: number, newStatus: Status){
    this.updatingUserId = userId;

    this.adminService.updateUserStatus(userId, newStatus).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
        const currentUsers = this.users$.getValue();
        const updateUsers = currentUsers.map(user =>
          user.userId === userId ? {...user, status: newStatus} : user);
        this.users$.next(updateUsers);
        if(newStatus === 'APPROVED'){
          this.toastr.success('Usuario aprobado. Se ha enviado un correo de notificación', '¡Aprobación Exitosa!');
        } else if(newStatus === 'REJECTED'){
          this.toastr.warning('La solicitud ha sido rechazada.', 'Usuario Rechazado')
        } else if(newStatus === 'SUSPENDED'){
          this.toastr.warning('El usuario ha sido suspendido del sistema.', 'Usuario Suspendido')
        }
        this.updatingUserId = null;
      },
      error: (error) => {
        this.toastr.error('Error al actualizar el estado del usuario', 'Error');
        this.updatingUserId = null;
        console.error(error);
      }
    })
  }

  goToStats(){
    this.router.navigate(['/reports']);
  }
}
