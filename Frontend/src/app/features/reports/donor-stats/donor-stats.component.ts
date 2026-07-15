import {Component, Input} from '@angular/core';
import {DonorStats} from '../../../shared/models/stats.model';

@Component({
  selector: 'app-donor-stats',
  imports: [],
  templateUrl: './donor-stats.component.html',
  styleUrl: './donor-stats.component.css'
})
export class DonorStatsComponent {
  @Input() stats!: DonorStats;
}
