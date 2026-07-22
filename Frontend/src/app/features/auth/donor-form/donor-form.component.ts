import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../../core/services/auth.service';
import {DonorTypeLookup, NeighborhoodLookup} from '../../../shared/models/donor.model';
import {DonorTypeTranslatePipe} from '../../../shared/pipes/donor-type-translate.pipe';
import {catchError, forkJoin, of} from 'rxjs';
import {NgClass} from '@angular/common';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {Router, RouterLink} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {BaseFormComponent} from "../../../shared/utils/base-form.component";
import {LocationService} from "../../../core/services/location.service";

@Component({
  selector: 'app-donor-form',
  imports: [
    ReactiveFormsModule,
    DonorTypeTranslatePipe,
    NgClass,
    RouterLink,
    FormsModule
  ],
  templateUrl: './donor-form.component.html',
  styleUrl: './donor-form.component.css'
})
export class DonorFormComponent extends BaseFormComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly locationService = inject(LocationService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly toastr = inject(ToastrService);
  private readonly router = inject(Router);

  donorForm!: FormGroup;
  isSubmitting = false;
  acceptedTerms = false;

  get form() {
    return this.donorForm;
  }

  donorTypes: DonorTypeLookup[] = [];
  neighborhoods: NeighborhoodLookup[] = [];

  /**
   * Initialize form and load initial data
   */
  ngOnInit(): void {
    this.initForm();
    this.loadInitialData();
  }

  /**
   * Initialize form
   */
  private initForm():void{
    this.donorForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      tradeName: ['', [Validators.required]],
      legalName: ['', [Validators.required]],
      taxId: ['', [Validators.required, Validators.pattern('^[0-9]+$'), Validators.minLength(11), Validators.maxLength(11) ]],
      phoneNumber: ['', [Validators.required]],
      donorType: ['', [Validators.required]],
      street: ['', [Validators.required]],
      streetNumber: ['', [Validators.required]],
      neighborhoodId: ['', [Validators.required]],
      floor: [null],
      apartment: [null],
      latitude: [null, Validators.required],
      longitude: [null, Validators.required],
      terms: [false, Validators.requiredTrue]
    })
  }

  /**
   * Load initial data from API (donor types and neighborhoods)
   */
  private loadInitialData(){
    forkJoin({
      types: this.authService.getDonorTypes().pipe(
        catchError(error => {
          return of([] as DonorTypeLookup[]);
        })
      ),
      neighborhoods: this.authService.getNeighborhoods().pipe(
        catchError(error => {
          return of([] as NeighborhoodLookup[]);
        })
      )
    }).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: ({types, neighborhoods}) => {
        this.donorTypes = types;
        this.neighborhoods = neighborhoods;
      }
    })
  }

  /**
   * Submit form
   */
  onSubmit():void{
    if(this.donorForm.invalid){
      this.donorForm.markAllAsTouched();
      this.toastr.warning('Por favor, completá los campos obligatorios.', 'Formulario incompleto.')
      return;
    }
    this.isSubmitting = true;
    const formData = this.donorForm.value;

    this.authService.registerDonor(formData).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.toastr.success('Tu cuenta ha sido creada exitosamente.', '¡Bienvenido a EcoNexo!')

          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 1500);
        },
        error: (error) => {
          this.isSubmitting = false;
          const backendMessage = error.error.message || '';
          if(error.status === 409 || backendMessage.includes('Donor already exists')){
            this.toastr.error('El email o CUIT ya se encuentra registrado.', 'Error de registro.')
          } else if(error.status === 400){
            this.toastr.warning('Asegúrese de ingresar todos los datos obligatorios. Intente de nuevo.', 'Error de registro.');
          } else {
            this.toastr.error('Ocurrió un problema en el servidor. Intente de nuevo.', 'Error.')
          }
        }
      })
  }

  /**
   * Handle address blur
   */
  async onAddressBlur(): Promise<void> {
    const street = this.donorForm.get('street')?.value;
    const number = this.donorForm.get('streetNumber')?.value;

    if (street && number) {
      const coords = await this.locationService.geocodeAddress(street, number);
      if (coords) {
        this.form.patchValue(coords);
        this.toastr.success('Ubicación detectada correctamente');
      } else {
        this.toastr.warning('No pudimos encontrar la dirección exacta');
      }
    }
  }

  initAutocomplete() {
    const input = document.querySelector('input[formControlName="street"]') as HTMLInputElement;
    const google = (window as any).google;

    if (google && google.maps && google.maps.places) {
      const autocomplete = new google.maps.places.Autocomplete(input, {
        componentRestrictions: { country: 'AR' },
        fields: ['geometry', 'formatted_address']
      });

      autocomplete.addListener('place_changed', () => {
        const place = autocomplete.getPlace();
        if (place.geometry) {
          this.donorForm.patchValue({
            latitude: place.geometry.location?.lat(),
            longitude: place.geometry.location?.lng()
          });
          this.donorForm.get('latitude')?.updateValueAndValidity();
          this.donorForm.get('longitude')?.updateValueAndValidity();
        }
      });
    }
  }

}
