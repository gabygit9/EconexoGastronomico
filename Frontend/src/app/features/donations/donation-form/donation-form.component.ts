import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {DonationService} from '../../../core/services/donation.service';
import {NavbarComponent} from '../../../shared/components/navbar/navbar.component';
import {FooterComponent} from '../../../shared/components/footer/footer.component';
import {AuthService} from '../../../core/services/auth.service';
import {AsyncPipe, formatDate, NgClass} from '@angular/common';
import {forkJoin, map} from 'rxjs';
import {FilterByCategoryPipe} from '../../../shared/pipes/filter-by-category.pipe';
import {BaseFormComponent} from '../../../shared/utils/base-form.component';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {Category, Product, UnitOfMeasure} from '../../../shared/models/donation.model';
import {timeWindowValidator} from '../../../shared/utils/time-window-validator';

@Component({
  selector: 'app-donation-form',
  imports: [
    ReactiveFormsModule,
    NavbarComponent,
    FooterComponent,
    AsyncPipe,
    FilterByCategoryPipe,
    NgClass
  ],
  templateUrl: './donation-form.component.html',
  styleUrl: './donation-form.component.css'
})
export class DonationFormComponent extends BaseFormComponent implements OnInit{
  private fb = inject(FormBuilder);
  private donationService = inject(DonationService);
  private authService = inject(AuthService);

  private readonly destroyRef = inject(DestroyRef);
  private readonly toastr = inject(ToastrService);
  private readonly router = inject(Router);

  userName$ = this.authService.currentUser$.pipe(
    map(profile => {
      if(profile && 'tradeName' in profile){
        return profile.tradeName;
      }
      return '';
    })
  );

  donationForm!: FormGroup
  isSubmitting = false;

  categories: Category[] = [];
  allProducts: Product[] = [];
  unitsOfMeasure: UnitOfMeasure[] = [];

  maxProductionDate!: string;
  minExpirationDate!: string;

  override get form(): FormGroup {
    return this.donationForm;
  }

  ngOnInit(){
    this.initForm();
    this.calculateDates();
    this.loadCatalogData();
    this.addItem();
  }

  initForm(){
    this.donationForm = this.fb.group({
      pickupStartTime: ['', Validators.required],
      pickupEndTime: ['', Validators.required],
      items: this.fb.array([])
    }, { validators: timeWindowValidator });
  }

  loadCatalogData(){
    forkJoin({
      categories: this.donationService.getCategories(),
      products: this.donationService.getProducts(),
      units: this.donationService.getUnitOfMeasures()
    }).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (response) => {
        this.categories = response.categories;
        this.allProducts = response.products;
        this.unitsOfMeasure = response.units;
      },
      error: (error) => {
        this.toastr.error('No se pudieron cargar los productos. Por favor, recargá la página.', 'Error de conexión');
      }
    })
  }

  get items(): FormArray{
    return this.donationForm.get('items') as FormArray;
  }

  calculateDates(){
    const now = new Date();
    const fiveDaysFromNow = new Date();
    fiveDaysFromNow.setDate(now.getDate() + 5);

    this.maxProductionDate = formatDate(now, 'yyyy-MM-dd', 'en-US')
    this.minExpirationDate = formatDate(now, 'yyyy-MM-dd', 'en-US')

    this.donationForm.get('pickupStartTime')?.valueChanges.subscribe(startDateStr => {
      if(startDateStr){
        const startDate = new Date(startDateStr + 'T00:00:00');
        const maxEndDate = new Date(startDate);
        maxEndDate.setDate(startDate.getDate() + 5);
      }
    })
  }

  createItem(): FormGroup {
    return this.fb.group({
      categoryId: [null, Validators.required],
      productId: [null, Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      unitOfMeasureId: [null, Validators.required],
      description: [null],
      batchNumber: [''],
      productionDate: [''],
      expirationDate: ['', Validators.required],
      deliveryTemperature: [''],
      allergenWarning: [''],
      observations: ['']
    });
  }

  onCategoryChange(index:number){
    const item = this.items.at(index) as FormGroup;

    item.get('productId')?.setValue(null);
    item.get('description')?.setValue(null);
    item.get('description')?.clearValidators();
    item.get('description')?.updateValueAndValidity();
  }

  onProductChange(index:number){
    setTimeout(() => {
      const item = this.items.at(index) as FormGroup;
      const productId = Number(item.get('productId')?.value);
      const descriptionControl = item.get('description');
      const batchControl = item.get('batchNumber');
      const selectedProduct = this.allProducts.find(p => p.id === productId);

      if(productId === 4 || productId === 5){
        descriptionControl?.setValidators([Validators.required]);
      }else{
        descriptionControl?.clearValidators();
        descriptionControl?.updateValueAndValidity();
      }
      if(selectedProduct?.isOriginalPackaging){
        batchControl?.setValidators([Validators.required]);
      }else{
        batchControl?.clearValidators();
      }

      batchControl?.updateValueAndValidity();
      descriptionControl?.updateValueAndValidity();
    });
  }

  addItem(){
    this.items.push(this.createItem());
  }

  removeItem(index:number){
    this.items.removeAt(index);
  }

  isInvalidItemField(index: number, field: string) {
    const control = this.items.at(index).get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  getItemErrorMessage(index: number, field: string) {
    const control = this.items.at(index).get(field);
    if (control && control.errors && (control.dirty || control.touched)) {
      if (control.errors['required']) return 'Este campo es obligatorio.';
      if (control.errors['min']) return `Mínimo permitido: ${control.errors['min'].min}`;
    }
    return '';
  }

  isRequiredField(index: number, field: string) {
    const control = this.items.at(index).get(field);
    return control?.hasValidator(Validators.required) ?? false;
  }

  onSubmit(){
    if(this.donationForm.invalid){
      this.donationForm.markAllAsTouched();
      //Marcar el interior del array
      this.items.controls.forEach(item => item.markAllAsTouched());
      this.toastr.warning("Por favor, complete todos los campos obligatorios.", "Formulario Incompleto");

      return;
    }

    this.isSubmitting = true;
    const formValue = this.donationForm.value;

    const itemsPayload = formValue.items.map((item: any) => {
      return {
        productId: Number(item.productId),
        quantity: Number(item.quantity),
        unitOfMeasureId: Number(item.unitOfMeasureId),
        description: (Number(item.productId) === 4 || Number(item.productId) === 5)? item.description : null,
        productionDate: item.productionDate ? `${item.productionDate}T00:00:00` : null,
        expirationDate: `${item.expirationDate}T23:59:59`,
        batchNumber: item.batchNumber || null,
        deliveryTemperature: item.deliveryTemperature || null,
        allergenWarning: item.allergenWarning || null,
        observations: item.observations || null
      };
    });

    const todayStr = formatDate(new Date(), 'yyyy-MM-dd', 'en-US');
    const requestPayload = {
      pickupStartTime: `${todayStr}T${formValue.pickupStartTime}:00`,
      pickupEndTime: `${todayStr}T${formValue.pickupEndTime}:00`,
      items: itemsPayload
    };

    this.donationService.donate(requestPayload).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response) => {
          this.toastr.success("Tu excedente ha sido publicado y está listo para ser rescatado.", "¡Donación Publicada!");
          setTimeout(() => {
            this.router.navigate(['/dashboard/donor']);
          }, 1500);
        },
        error: (error) => {
          this.isSubmitting = false;
          if (error.status === 400) {
            this.toastr.warning('Verificá que los datos del catálogo ingresados sean correctos.', 'Error de publicación');
          } else {
            this.toastr.error('Ocurrió un problema en el servidor. Intente de nuevo más tarde.', 'Error del sistema');
          }
        }
      })
  }
}
