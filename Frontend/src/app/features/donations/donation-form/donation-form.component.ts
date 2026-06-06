import {Component, inject, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {DonationService} from '../../../core/services/donation.service';
import {NavbarComponent} from '../../../shared/components/navbar/navbar.component';
import {DonorResponse} from '../../../shared/models/donor.model';
import {FooterComponent} from '../../../shared/components/footer/footer.component';
import {AuthService} from '../../../core/services/auth.service';
import {AsyncPipe} from '@angular/common';
import {map} from 'rxjs';

@Component({
  selector: 'app-donation-form',
  imports: [
    ReactiveFormsModule,
    NavbarComponent,
    FooterComponent,
    AsyncPipe
  ],
  templateUrl: './donation-form.component.html',
  styleUrl: './donation-form.component.css'
})
export class DonationFormComponent implements OnInit{
  private fb = inject(FormBuilder);
  private donationService = inject(DonationService);
  private authService = inject(AuthService);

  userName$ = this.authService.currentUser$.pipe(
    map(profile => {
      if(profile && 'tradeName' in profile){
        return profile;
      }
      return '';
    })
  );

  donationForm!: FormGroup;

  ngOnInit(){
    this.donationForm = this.fb.group({
      pickupStartTime: ['', Validators.required],
      pickupEndTime: ['', Validators.required],
      items: this.fb.array([])
    });

    this.addItem();
  }

  get items(): FormArray{
    return this.donationForm.get('items') as FormArray;
  }

  createItem(): FormGroup {
    return this.fb.group({
      productId: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      batchNumber: [''],
      productionDate: [''],
      expirationDate: ['', Validators.required],
      deliveryTemperature: [''],
      allergenWarning: ['Ninguna'],
      observations: ['']
    });
  }

  addItem(){
    this.items.push(this.createItem());
  }

  removeItem(index:number){
    this.items.removeAt(index);
  }

  onSubmit(){
    if(this.donationForm.valid){
      console.log('JSON a enviar: ', this.donationForm.value);
    } else {
      this.donationForm.markAllAsTouched();
    }
  }
}
