import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {BaseFormComponent} from '../../../shared/utils/base-form.component';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../../core/services/auth.service';
import {LocationService} from '../../../core/services/location.service';
import {ToastrService} from 'ngx-toastr';
import {Router} from '@angular/router';
import {NgClass} from '@angular/common';
import {NeighborhoodLookup} from '../../../shared/models/donor.model';
import {catchError, delay, of} from 'rxjs';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-driver-form',
  imports: [
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './driver-form.component.html',
  styleUrl: './driver-form.component.css'
})
export class DriverFormComponent extends BaseFormComponent implements OnInit{

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly locationService = inject(LocationService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly toastr = inject(ToastrService);
  private readonly router = inject(Router);

  driverForm!: FormGroup;
  isSubmitting = false;

  get form(){
    return this.driverForm;
  }

  neighborhoods: NeighborhoodLookup[] = [];

  /**
   * Initialize form and load initial data
   */
  ngOnInit(): void {
    this.initForm();
    this.setupVehicleValidation();
    this.loadInitialData();
  }

  private initForm(){
    this.driverForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      taxId: ['', [Validators.required, Validators.pattern('^[0-9]+$'), Validators.minLength(11), Validators.maxLength(11) ]],
      birthDate: ['', [Validators.required]],
      foodHandlerCertificateUrl: ['', [Validators.required]],
      foodHandlerCertificateExpiration: ['', [Validators.required]],
      phoneNumber: ['', [Validators.required]],
      street: ['', [Validators.required]],
      streetNumber: ['', [Validators.required]],
      floor: [null],
      apartment: [null],
      latitude: [null, Validators.required],
      longitude: [null, Validators.required],
      neighborhoodId: ['', [Validators.required]],
      vehicle: this.fb.group({
        vehicleType: ['', [Validators.required]],
        hasRefrigeration: [false],
        capacityKg: ['', [Validators.required, Validators.min(1)]],
        numberPlate: [''],
        driverLicenseFrontUrl: [''],
        driverLicenseBackUrl: [''],
        driverLicenseExpiration: ['']
      })
    })
  }

  /**
   * Load initial data for the form
   */
  private loadInitialData(){
    this.authService.getNeighborhoods().pipe(
      takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (n) => {
        this.neighborhoods = n;
      },
      error: (error) => {
        console.error("Error loading neighborhoods.", error);
        this.neighborhoods = [];
      }
    })
  }

  /**
   * Handle form submission
   */
  onSubmit(){
    if(this.driverForm.invalid){
      this.driverForm.markAllAsTouched();
      this.toastr.warning("Por favor, completá los campos obligatorios", "Formulario Incompleto.");
      return;
    }
    this.isSubmitting = true;
    const formData = this.driverForm.value;

    this.authService.registerDriver(formData).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (response) => {
        this.toastr.success("Tu cuenta ha sido creada exitosamente.", "¡Bienvenido a EcoNexo!")
        setTimeout(() => {
          this.router.navigate(['/login'])
        }, 1500);
      },
      error: (error) => {
        this.isSubmitting = false;
        const backendMessage = error.error.message || '';
        if(error.status === 409 || backendMessage.includes('Driver already exists')){
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
    const neighborhoodId = Number(this.driverForm.get('neighborhoodId')?.value);
    const coords = this.locationService.getMockCoordinates(neighborhoodId);

    if(coords){
      this.form.patchValue(coords);
      console.log('Coords assigned provisionally:', coords);
    }
  }

  /**
   * Handle address blur temporary until Google Geocoding API is implemented
   */
  //Todo reemplazar en el sprint 3 con conexión real a Google API
  async onAddressBlur(): Promise<void> {
    const street = this.driverForm.get('street')?.value;
    const number = this.driverForm.get('streetNumber')?.value;

    const coords = await this.locationService.geocodeAddress(street, number);
    if (coords) {
      this.form.patchValue(coords);
    }
  }

  /**
   * Setup vehicle validation
   */
  private setupVehicleValidation() {
    const vehicleTypeControl = this.driverForm.get('vehicle.vehicleType');
    const plateControl = this.driverForm.get('vehicle.numberPlate');
    const licenseExpControl = this.driverForm.get('vehicle.driverLicenseExpiration');

    vehicleTypeControl?.valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(type => {
      if(type === 'BICYCLE' || type === 'KICK_SCOOTER'){
        plateControl?.clearValidators();
        licenseExpControl?.clearValidators();
      }else{
        plateControl?.setValidators([Validators.required]);
        licenseExpControl?.setValidators([Validators.required]);
      }
      plateControl?.updateValueAndValidity();
      licenseExpControl?.updateValueAndValidity();
    })
  }


}
