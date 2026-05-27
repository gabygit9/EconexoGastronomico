import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../../core/services/auth.service';
import {DonorTypeLookup, NeighborhoodLookup} from '../../../shared/models/donor.model';
import {DonorTypeTranslatePipe} from '../../../shared/pipes/donor-type-translate.pipe';
import {catchError, forkJoin, of} from 'rxjs';
import {NgClass} from '@angular/common';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {Router, RouterLink} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {BaseFormComponent} from "../../../shared/utils/base-form.component";

@Component({
  selector: 'app-donor-form',
  imports: [
    ReactiveFormsModule,
    DonorTypeTranslatePipe,
    NgClass,
    RouterLink
  ],
  templateUrl: './donor-form.component.html',
  styleUrl: './donor-form.component.css'
})
export class DonorFormComponent extends BaseFormComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly toastr = inject(ToastrService);
  private readonly router = inject(Router);

  donorForm!: FormGroup;
  isSubmitting = false;

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
      taxId: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
      phoneNumber: ['', [Validators.required]],
      donorType: ['', [Validators.required]],
      street: ['', [Validators.required]],
      streetNumber: ['', [Validators.required]],
      neighborhoodId: ['', [Validators.required]],
      floor: [null],
      apartment: [null],
      latitude: [null, Validators.required],
      longitude: [null, Validators.required]
    })
  }

  /**
   * Load initial data from API (donor types and neighborhoods)
   */
  private loadInitialData(){
    forkJoin({
      types: this.authService.getDonorTypes().pipe(
        catchError(error => {
          console.error("Error loading donor types", error);
          return of([] as DonorTypeLookup[]);
        })
      ),
      neighborhoods: this.authService.getNeighborhoods().pipe(
        catchError(error => {
          console.error("Error loading neighborhoods", error);
          return of([] as NeighborhoodLookup[]);
        })
      )
    }).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: ({types, neighborhoods}) => {
        this.donorTypes = types;
        this.neighborhoods = neighborhoods;
      },
      error: (error) => console.error("Error loading donor types", error)
    })
  }

  /**
   * Method that call Google Geocoding API
   * takes street, streetNumber and city from form and returns latitude and longitude, updating form
   */
  async geocodeAddress(): Promise<void> {
    const street = this.donorForm.get('street')?.value;
    const number = this.donorForm.get('streetNumber')?.value;

    if(!street || !number) return;

    //todo implementar llamada Google Geocoding API
    //ej
    //const coords = await this.googleService.getCoordinates(`${street} ${number}, Córdoba, Argentina`);
    //this.donorForm.patchValue({ latitude: coords.lat, longitude: coords.lng });
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
        next: (response) => {
          this.toastr.success('Tu cuenta ha sido creada exitosamente.', '¡Bienvenido a EcoNexo!')

          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 1500);
        },
        error: (error) => {
          this.isSubmitting = false;
          const backendMessage = error.error.message || '';
          if(error.status === 409 || backendMessage.includes('Donor already exists')) {
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
   * Handle neighborhood change temporary until Google Geocoding API is implemented
   */
  //Todo reemplazar en el sprint 3 con conexión real a Google API
  onNeighborhoodChange(): void {
    const neighborhoodId = Number(this.donorForm.get('neighborhoodId')?.value);

    // Inyección automática para simular la localización y pasar el Validator.required
    if (neighborhoodId === 1) {
      this.donorForm.patchValue({ latitude: -31.4233, longitude: -64.1865 });
    } else if (neighborhoodId === 2) {
      this.donorForm.patchValue({ latitude: -31.4125, longitude: -64.1678 });
    }

    console.log('Coordenadas asignadas provisionalmente:', this.donorForm.value.latitude, this.donorForm.value.longitude);
  }

  /**
   * Handle address blur temporary until Google Geocoding API is implemented
   */
  //Todo reemplazar en el sprint 3 con conexión real a Google API
  onAddressBlur(): void {
    const street = this.donorForm.get('street')?.value;
    const number = this.donorForm.get('streetNumber')?.value;

    if (street && number) {
      // Acá se va a disparar la llamada real a: google.maps.Geocoder
      console.log(`Listo para geocodificar dirección: ${street} ${number}, Córdoba, Argentina`);
    }
  }

}
