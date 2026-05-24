import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../../core/services/auth.service';
import {ToastrService} from 'ngx-toastr';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [RouterLink, ReactiveFormsModule, NgClass],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit{

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly toastr = inject(ToastrService);
  private readonly destroyRef = inject(DestroyRef)

  loginForm!: FormGroup;
  isSubmitting = false;

  ngOnInit(){
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    })
  }

  isInvalidField(field: string){
    const control = this.loginForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  getErrorMessage(field: string) {
    const control = this.loginForm.get(field);
    if (control && control.errors && (control.dirty || control.touched)) {
      if (control.errors['required']) return 'Este campo es obligatorio.';
      if (control.errors['email']) return 'El formato del email no es válido.';
    }
    return ''
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
         this.toastr.success(data.message || 'Ingreso exitoso', '¡Bienvenido de nuevo!');
         //todo crear perfiles dashboard
         this.router.navigate(['/dashboard/donor']);
       }else{
         this.toastr.error(data.message || 'Error al iniciar sesión', 'Error');
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
