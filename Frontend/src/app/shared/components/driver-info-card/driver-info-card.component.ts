import {Component, input} from '@angular/core';
import {DriverSummaryDTO} from '../../models/driver.model';
import {VehicleTypeTranslatePipe} from '../../pipes/vehicle-type-translate.pipe';

@Component({
  selector: 'app-driver-info-card',
  imports: [
    VehicleTypeTranslatePipe
  ],
  templateUrl: './driver-info-card.component.html',
  styleUrl: './driver-info-card.component.css'
})
export class DriverInfoCardComponent {

  info = input.required<DriverSummaryDTO>();

}
