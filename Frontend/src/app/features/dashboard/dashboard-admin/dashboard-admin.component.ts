import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {AdminService} from '../../../core/services/admin.service';
import {UserAdminResponse} from '../../../shared/models/admin.model';
import {BehaviorSubject, map} from 'rxjs';
import {Status} from '../../../shared/models/login.model';
import {AsyncPipe, DatePipe, NgClass} from '@angular/common';
import {ToastrService} from 'ngx-toastr';
import {AuthService} from '../../../core/services/auth.service';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {NavbarComponent} from '../../../shared/components/navbar/navbar.component';
import {FooterComponent} from '../../../shared/components/footer/footer.component';
import {StatusTranslatePipe} from '../../../shared/pipes/status-translate.pipe';
import {RoleTranslatePipe} from '../../../shared/pipes/role-translate.pipe';

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

  users$ = new BehaviorSubject<UserAdminResponse[]>([]);
  isLoading = true;

  userName$ = this.authService.currentUser$.pipe(
    map(profile => {
      if (profile && 'email' in profile) {
        return profile.email;
      }
      return "Administrador";
    })
  );

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
    this.adminService.updateUserStatus(userId, newStatus).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
        const currentUsers = this.users$.getValue();
        const updateUsers = currentUsers.map(user =>
          user.userId === userId ? {...user, status: newStatus} : user);
        this.users$.next(updateUsers);
        this.toastr.success('Estado actualizado con éxito', 'Estado Actualizado');
      },
      error: (error) => {
        this.toastr.error('Error al actualizar el estado del usuario', 'Error');
        console.error(error);
      }
    })
  }

  protected readonly RoleTranslatePipe = RoleTranslatePipe;
  protected readonly StatusTranslatePipe = StatusTranslatePipe;
}
