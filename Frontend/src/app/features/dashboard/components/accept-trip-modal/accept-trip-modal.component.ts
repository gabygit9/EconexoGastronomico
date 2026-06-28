import {Component, EventEmitter, Input, OnInit, Output, signal} from '@angular/core';
import {DonationResponse} from '../../../../shared/models/donation.model';
import {VehicleResponseDTO} from '../../../../shared/models/driver.model';
import {DatePipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {VehicleTypeTranslatePipe} from '../../../../shared/pipes/vehicle-type-translate.pipe';

@Component({
  selector: 'app-accept-trip-modal',
  imports: [
    DatePipe,
    FormsModule,
    VehicleTypeTranslatePipe
  ],
  templateUrl: './accept-trip-modal.component.html',
  styleUrl: './accept-trip-modal.component.css'
})
export class AcceptTripModalComponent implements OnInit{

  @Input({ required: true }) trip!: DonationResponse;
  @Input({ required: true }) vehicles: VehicleResponseDTO[] = [];
  @Input() isAccepting = false;

  @Output() close = new EventEmitter<void>();
  @Output() accept = new EventEmitter<number>();

  selectedVehicleId = signal<number | null>(null);

  ngOnInit(){
    if(this.vehicles && this.vehicles.length > 0){
      this.selectedVehicleId.set(this.vehicles[0].id);
    }
  }

  onConfirm(){
    const vId = this.selectedVehicleId();
    if(vId){
      this.accept.emit(vId);
    }
  }
}
