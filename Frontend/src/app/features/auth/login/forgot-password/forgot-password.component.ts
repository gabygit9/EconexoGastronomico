import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {BaseFormComponent} from '../../../../shared/utils/base-form.component';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../../../core/services/auth.service';
import {ToastrService} from 'ngx-toastr';
import {RouterLink} from '@angular/router';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-forgot-password',
  imports: [
    NgClass,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})
export class ForgotPasswordComponent extends BaseFormComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly  toastr = inject(ToastrService);
  private readonly destroyRef = inject(DestroyRef);

  forgotPasswordForm! : FormGroup;
  isSubmitting = false;
  emailSent = false;

  get form(){
    return this.forgotPasswordForm;
  }

  ngOnInit() {
    this.forgotPasswordForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit() {
   if(this.forgotPasswordForm.invalid){
     this.forgotPasswordForm.markAllAsTouched();
     return;
   }

   this.isSubmitting = true;
   const email = this.forgotPasswordForm.value.email;

   this.authService.requestPasswordReset(email).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
     next: response => {
       this.isSubmitting = false;
       this.emailSent = true;
       this.toastr.success('Email enviado con éxito','Solicitud Procesada.');
     },
     error: err => {
       this.isSubmitting = false;
       this.toastr.error('Ocurrió un problema de conexión. Intente nuevamente.','Error');
     }
   })
  }

}
