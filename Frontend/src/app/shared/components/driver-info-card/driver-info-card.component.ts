import {Component, input} from '@angular/core';
import {DriverSummaryDTO} from '../../models/driver.model';

@Component({
  selector: 'app-driver-info-card',
  imports: [],
  templateUrl: './driver-info-card.component.html',
  styleUrl: './driver-info-card.component.css'
})
export class DriverInfoCardComponent {

  info = input.required<DriverSummaryDTO>();

}
