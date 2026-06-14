import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {BaseFormComponent} from '../../../../shared/utils/base-form.component';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {AuthService} from '../../../../core/services/auth.service';
import {ToastrService} from 'ngx-toastr';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-reset-password',
  imports: [
    RouterLink,
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css'
})
export class ResetPasswordComponent extends BaseFormComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly authService = inject(AuthService);
  private readonly toastr = inject(ToastrService);
  private readonly destroyRef = inject(DestroyRef);

  resetForm!: FormGroup;
  token: string | null = null;
  isSubmitting = false;
  success = false;

  showPassword = false;
  showConfirmPassword = false;

  get form(){
    return this.resetForm;
  }

  ngOnInit(){
    //Capturar token de la URL
    this.token = this.route.snapshot.queryParamMap.get('token');

    if(!this.token){
      this.toastr.error('No se proporciono un token válido','Error');
    }

    this.resetForm = this.fb.group({
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]}, { validators: this.passwordMatchValidator });
  }

  private passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  onSubmit(){
   if(this.resetForm.invalid || !this.token){
     this.resetForm.markAllAsTouched();
     if(!this.token) this.toastr.error('No se puede procesar sin un token válido.', 'Error');
     return;
   }

   this.isSubmitting = true;
   const newPassword = this.resetForm.value.password;

   this.authService.confirmPassword(this.token, newPassword).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
     next: response => {
       this.isSubmitting = false;
       this.success = true;
       this.toastr.success('Contraseña actualizada correctamente.', '¡Éxito!');
     },
     error: err => {
       this.isSubmitting = false;
       const errorMessage = err.error?.error || 'Ocurrió un problema al restablecer la contraseña.';
       this.toastr.error(errorMessage, 'Error');
       },

   })
  }

}
