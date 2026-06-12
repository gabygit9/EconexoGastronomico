import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../../core/services/auth.service';
import {ToastrService} from 'ngx-toastr';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {NgClass} from '@angular/common';
import {BaseFormComponent} from "../../../shared/utils/base-form.component";

@Component({
  selector: 'app-login',
  imports: [RouterLink, ReactiveFormsModule, NgClass],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent extends BaseFormComponent implements OnInit{

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly toastr = inject(ToastrService);
  private readonly destroyRef = inject(DestroyRef)

  loginForm!: FormGroup;
  isSubmitting = false;

  get form() {
    return this.loginForm;
  }

  ngOnInit(){
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    })
  }

  onSubmit() {
    if(this.loginForm.invalid){
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    const credentials = this.loginForm.value;

    this.authService.login(credentials).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: data => {
        if(data.status){
          localStorage.setItem('econexo_token', data.jwt);

          try{
            const tokenPayload = JSON.parse(atob(data.jwt.split('.')[1]));

            const authoritiesString = tokenPayload.authorities || '';

            if(authoritiesString.includes('ROLE_NGO')){
              this.router.navigate(['/dashboard/ngo']);
            }else if(authoritiesString.includes('ROLE_DONOR')){
              this.router.navigate(['/dashboard/donor']);
            }else if(authoritiesString.includes('ROLE_DRIVER')){
              this.router.navigate(['/dashboard/driver']);
            }else if(authoritiesString.includes('ROLE_ADMIN')){
              this.router.navigate(['/dashboard/admin']);
            }else{
              this.router.navigate(['/']);
            }
          }catch (e){
            console.error('Error decodifying token', e);
            this.router.navigate(['/']);
          }
        }else{
          this.toastr.error('Error al iniciar sesión', 'Error');
          this.isSubmitting = false;
        }
      },
      error: err => {
        this.isSubmitting = false;
        if(err.status === 401 || err.status === 403){
          this.toastr.error('Email o contraseña incorrectos', 'Acceso denegado');
        }else{
          this.toastr.error('Ocurrió un problema en el servidor. Intente de nuevo.', 'Error');
        }
      }
    })

  }


}
