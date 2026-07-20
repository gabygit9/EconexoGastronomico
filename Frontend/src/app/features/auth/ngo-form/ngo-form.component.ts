import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {BaseFormComponent} from '../../../shared/utils/base-form.component';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgoTypeLookup} from '../../../shared/models/ngo.model';
import {NeighborhoodLookup} from '../../../shared/models/donor.model';
import {AuthService} from '../../../core/services/auth.service';
import {ToastrService} from 'ngx-toastr';
import {Router, RouterLink} from '@angular/router';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {NgClass} from '@angular/common';
import {NgoTypeTranslatePipe} from '../../../shared/pipes/ngo-type-translate.pipe';
import {catchError, forkJoin, of} from 'rxjs';
import {LocationService} from '../../../core/services/location.service';

@Component({
  selector: 'app-ngo-form',
  imports: [
    ReactiveFormsModule,
    NgoTypeTranslatePipe,
    RouterLink,
    NgClass,
    FormsModule
  ],
  templateUrl: './ngo-form.component.html',
  styleUrl: './ngo-form.component.css'
})
export class NgoFormComponent extends BaseFormComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly locationService = inject(LocationService);
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
  acceptedTerms = false;

  ngOnInit() {
    this.initForm();
    this.loadInitialData();
  }

  private initForm(){
    this.ngoForm = this.fb.group({
      ngoName: ['', Validators.required],
      taxId: ['', [Validators.required, Validators.pattern('^[0-9]+$'), Validators.minLength(11), Validators.maxLength(11)]],
      legalPersonalityNumber: ['', Validators.required],
      responsibleName: ['', Validators.required],
      street: ['', Validators.required],
      streetNumber: ['', Validators.required],
      floor: [null],
      apartment: [null],
      phoneNumber: ['', Validators.required],
      neighborhoodId: ['', Validators.required],
      latitude: [null, Validators.required],
      longitude: [null, Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required, Validators.minLength(8)],
      ngoType: ['', Validators.required],
      terms: [false, Validators.requiredTrue]
    })
  }

  private loadInitialData(){
    forkJoin({
      types: this.authService.getNgoTypes().pipe(
        catchError(err => {
          console.error("Error loading ngo types", err);
          return of([] as NgoTypeLookup[]);
        })
      ),
      neighborhoods: this.authService.getNeighborhoods().pipe(
        catchError(err => {
          console.error("Error loading neighborhoods", err);
          return of([] as NeighborhoodLookup[]);
        })
      )
    }).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: ({types, neighborhoods}) => {
        this.ngoTypes = types;
        this.neighborhoods = neighborhoods;
      },
      error: (err) => {
        console.error("Error loading initial data", err);
      }
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

  /**
   * Handle address blur
   */
  async onAddressBlur(): Promise<void> {
    const street = this.ngoForm.get('street')?.value;
    const number = this.ngoForm.get('streetNumber')?.value;

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
          this.ngoForm.patchValue({
            latitude: place.geometry.location?.lat(),
            longitude: place.geometry.location?.lng()
          });
          this.ngoForm.get('latitude')?.updateValueAndValidity();
          this.ngoForm.get('longitude')?.updateValueAndValidity();
        }
      });
    }
  }


}
