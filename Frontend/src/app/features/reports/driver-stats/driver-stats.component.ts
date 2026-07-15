import {Component, Input} from '@angular/core';
import {DriverStats} from '../../../shared/models/stats.model';

@Component({
  selector: 'app-driver-stats',
  imports: [],
  templateUrl: './driver-stats.component.html',
  styleUrl: './driver-stats.component.css'
})
export class DriverStatsComponent {
  @Input() stats!: DriverStats;
}
