import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {BaseFormComponent} from '../../../shared/utils/base-form.component';
import {FormBuilder, FormGroup} from '@angular/forms';
import {NgoTypeLookup} from '../../../shared/models/ngo.model';
import {NeighborhoodLookup} from '../../../shared/models/donor.model';
import {AuthService} from '../../../core/services/auth.service';
import {ToastrService} from 'ngx-toastr';
import {Router} from '@angular/router';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-ngo-form',
  imports: [],
  templateUrl: './ngo-form.component.html',
  styleUrl: './ngo-form.component.css'
})
export class NgoFormComponent extends BaseFormComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly toastr = inject(ToastrService);
  private readonly router = inject(Router);

  ngoForm!: FormGroup;
  isSubmitting = false;

  get form(){
    return this.ngoForm;
  }

  ngoTypes: NgoTypeLookup[] = [];
  neighborhoods: NeighborhoodLookup[] = [];

  ngOnInit() {
    this.initForm();
  }

  private initForm(){
    this.ngoForm = this.fb.group({
      ngoName: '',
      taxId: '',
      legalPersonalityNumber: '',
      responsibleName: '',
      street: '',
      streetNumber: '',
      floor: null,
      apartment: null,
      phoneNumber: '',
      neighborhoodId: null,
      latitude: null,
      longitude: null,
      email: '',
      password: '',
      ngoType: ''
    })
  }

  onSubmit(){
   if(this.ngoForm.invalid){
     this.ngoForm.markAllAsTouched();
     this.toastr.warning('Por favor, completá los campos obligatorios.', 'Formulario incompleto');
     return;
   }

   this.isSubmitting = true;
   const ngoData = this.ngoForm.value;

   this.authService.registerNgo(ngoData).pipe(takeUntilDestroyed(this.destroyRef))
     .subscribe({
       next: (response) => {
         this.toastr.success('Tu cuenta ha sido creada exitosamente.', 'Bienvenido a EcoNexo!');

         setTimeout(() => {
           this.router.navigate(['/login']);
         }, 1500);
       },
       error: (error) => {
         this.isSubmitting = false;
         const backendMessage = error.error.message || '';
         if(error.status === 409 || backendMessage.includes('Ngo already exists.')){
           this.toastr.error('La ONG ya se encuentra registrada.', 'Error de registro.');
         } else if(error.status === 400){
           this.toastr.warning('Asegúrese de ingresar todos los datos obligatorios. Intente de nuevo.', 'Error de registro.');
         } else {
           this.toastr.error('Ocurrió un problema en el servidor. Intente de nuevo.', 'Error.');
         }
       }
     })
  }
}
