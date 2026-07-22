import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {BaseFormComponent} from '../../../shared/utils/base-form.component';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../../core/services/auth.service';
import {LocationService} from '../../../core/services/location.service';
import {ToastrService} from 'ngx-toastr';
import {Router, RouterLink} from '@angular/router';
import {NgClass} from '@angular/common';
import {NeighborhoodLookup} from '../../../shared/models/donor.model';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {UploadService} from '../../../core/services/upload.service';
import {forkJoin, Observable, of, switchMap} from 'rxjs';

@Component({
  selector: 'app-driver-form',
  imports: [
    ReactiveFormsModule,
    NgClass,
    FormsModule,
    RouterLink
  ],
  templateUrl: './driver-form.component.html',
  styleUrl: './driver-form.component.css'
})
export class DriverFormComponent extends BaseFormComponent implements OnInit{

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly locationService = inject(LocationService);
  private readonly uploadService = inject(UploadService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly toastr = inject(ToastrService);
  private readonly router = inject(Router);

  selectedFoodHandlerFile: File | null = null;
  selectedLicenseFrontFile: File | null = null;
  selectedLicenseBackFile: File | null = null;

  driverForm!: FormGroup;
  isSubmitting = false;
  todayStr = '';
  maxBirthDateStr = '';

  acceptedTerms = false;

  get form(){
    return this.driverForm;
  }

  neighborhoods: NeighborhoodLookup[] = [];

  readonly vehicleTypes = [
    { value: 'CAR', label: 'Auto' },
    { value: 'TRUCK', label: 'Camión' },
    { value: 'BICYCLE', label: 'Bicicleta' },
    { value: 'MOTORCYCLE', label: 'Moto' },
    { value: 'KICK_SCOOTER', label: 'Monopatín' },
    { value: 'PICKUP', label: 'Camioneta' }
  ];

  /**
   * Initialize form and load initial data
   */
  ngOnInit(): void {
    this.calculateDateLimits();
    this.initForm();
    this.setupVehicleValidation();
    this.loadInitialData();
  }

  private calculateDateLimits(){
    const today = new Date();
    this.todayStr = today.toISOString().split('T')[0];
    const maxBirth = new Date;
    maxBirth.setFullYear(maxBirth.getFullYear() - 18);
    this.maxBirthDateStr = maxBirth.toISOString().split('T')[0];
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
      terms: [false, Validators.requiredTrue],
      vehicle: this.fb.group({
        vehicleType: ['', [Validators.required]],
        hasRefrigeration: [false],
        capacityKg: ['', [Validators.required, Validators.min(1)]],
        numberPlate: [''],
        driversLicenseFrontUrl: [''],
        driversLicenseBackUrl: [''],
        driversLicenseExpiration: ['']
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

    //Preparar subida de archivos, solo si hay archivos seleccionados
    const uploadTasks: {[key: string] : Observable<{ url: string}> } = {};

    if(this.selectedFoodHandlerFile){
      uploadTasks['foodHandler'] = this.uploadService.uploadFile(this.selectedFoodHandlerFile, "drivers");
    }
    if(this.selectedLicenseFrontFile){
      uploadTasks['licenseFront'] = this.uploadService.uploadFile(this.selectedLicenseFrontFile, "vehicles");
    }
    if(this.selectedLicenseBackFile){
      uploadTasks['licenseBack'] = this.uploadService.uploadFile(this.selectedLicenseBackFile, "vehicles");
    }

    //Subir archivos
    const executeUploads$ = Object.keys(uploadTasks).length > 0 ? forkJoin(uploadTasks) : of({});

    executeUploads$.pipe(
      switchMap((cloudinaryResponses: any)=> {
        const finalPayload = { ...this.driverForm.value };

        if(cloudinaryResponses.foodHandler){
          finalPayload.foodHandlerCertificateUrl = cloudinaryResponses.foodHandler.url;
        }
        if(cloudinaryResponses.licenseFront){
          finalPayload.vehicle.driversLicenseFrontUrl = cloudinaryResponses.licenseFront.url;
        }
        if(cloudinaryResponses.licenseBack){
          finalPayload.vehicle.driversLicenseBackUrl = cloudinaryResponses.licenseBack.url;
        }

        if(finalPayload.vehicle && finalPayload.vehicle.numberPlate === ''){
          finalPayload.vehicle.numberPlate = null;
        }
        return this.authService.registerDriver(finalPayload);
      }), takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (response) => {
        this.toastr.success("Tu cuenta ha sido creada exitosamente.", "¡Bienvenido a EcoNexo!")
        setTimeout(() => {
          this.router.navigate(['/login'])
        }, 1500);
      },
      error: (error) => {
        this.isSubmitting = false;
        const backendMessage = error.error.message || error.error || '';

        if(backendMessage.includes("18 years")){
          this.toastr.error("Debes ser mayor de 18 años para registrarte.", "Edad no permitida.")
        }else if(error.status === 409 || backendMessage.includes('Driver already exists')){
          this.toastr.error('El email o CUIT ya se encuentra registrado.', 'Error de registro.')
        }else if(error.status === 400){
          this.toastr.warning('Asegúrese de ingresar todos los datos obligatorios. Intente de nuevo.', 'Error de registro.');
        }else {
          this.toastr.error('Ocurrió un problema en el servidor. Intente de nuevo.', 'Error.')
        }
      }
    })
  }

  /**
   * Handle address blur
   */
  async onAddressBlur(): Promise<void> {
    const street = this.driverForm.get('street')?.value;
    const number = this.driverForm.get('streetNumber')?.value;

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
          this.driverForm.patchValue({
            latitude: place.geometry.location?.lat(),
            longitude: place.geometry.location?.lng()
          });
          this.driverForm.get('latitude')?.updateValueAndValidity();
          this.driverForm.get('longitude')?.updateValueAndValidity();
        }
      });
    }
  }

  /**
   * Setup vehicle validation
   */
  private setupVehicleValidation() {
    const vehicleTypeControl = this.driverForm.get('vehicle.vehicleType');
    const plateControl = this.driverForm.get('vehicle.numberPlate');
    const licenseFrontControl = this.driverForm.get('vehicle.driversLicenseFrontUrl');
    const licenseBackControl = this.driverForm.get('vehicle.driversLicenseBackUrl');
    const licenseExpControl = this.driverForm.get('vehicle.driversLicenseExpiration');

    vehicleTypeControl?.valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(type => {
      if(type === 'BICYCLE' || type === 'KICK_SCOOTER'){
        plateControl?.clearValidators();
        licenseExpControl?.clearValidators();
        licenseFrontControl?.clearValidators();
        licenseBackControl?.clearValidators();

        plateControl?.setValue(null);
        licenseExpControl?.setValue(null);
        licenseFrontControl?.setValue(null);
        licenseBackControl?.setValue(null);
      }else{
        plateControl?.setValidators([Validators.required]);
        licenseExpControl?.setValidators([Validators.required]);
        licenseFrontControl?.setValidators([Validators.required]);
        licenseBackControl?.setValidators([Validators.required]);
      }
      plateControl?.updateValueAndValidity();
      licenseExpControl?.updateValueAndValidity();
      licenseFrontControl?.updateValueAndValidity();
      licenseBackControl?.updateValueAndValidity();
    })
  }

  /**
   * Handle file selection
   * @param event
   * @param documentType
   */
  onFileSelected(event:any, documentType: string){
    const file = event.target.files[0];
    if(file){
      //Máximo 5MB
      if(file.size > 5 * 1024 * 1024){
        this.toastr.warning('El archivo es demasiado grande. Máximo 5MB.', 'Archivo muy pesado.');
        return;
      }

      if(documentType === 'foodHandler'){
        this.selectedFoodHandlerFile = file;
        const control = this.driverForm.get('foodHandlerCertificateUrl');
        control?.setValue(file.name);
        control?.markAsTouched();
        control?.updateValueAndValidity();
      } else if(documentType === 'licenseFront'){
        this.selectedLicenseFrontFile = file;
        const control = this.driverForm.get('vehicle.driversLicenseFrontUrl');
        control?.setValue(file.name);
        control?.markAsTouched();
        control?.updateValueAndValidity();
      }else if(documentType === 'licenseBack'){
        this.selectedLicenseBackFile = file;
        const control = this.driverForm.get('vehicle.driversLicenseBackUrl');
        control?.setValue(file.name);
        control?.markAsTouched();
        control?.updateValueAndValidity();
      }
    }
  }

  /**
   * Clear the selected file
   * @param documentType
   */
  clearFile(documentType: string){
    if(documentType === 'foodHandler'){
      this.selectedFoodHandlerFile = null;
      this.driverForm.patchValue({foodHandlerCertificate: ' '});
    }else if(documentType === 'licenseFront'){
      this.selectedLicenseFrontFile = null;
      this.driverForm.get('vehicle')?.patchValue({driversLicenseFrontUrl: ' '});
    }else if(documentType === 'licenseBack'){
      this.selectedLicenseBackFile = null;
      this.driverForm.get('vehicle')?.patchValue({driversLicenseBackUrl: ' '});
    }
  }


}
