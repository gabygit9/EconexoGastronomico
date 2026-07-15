import {Component, Input} from '@angular/core';
import {NgoStats} from '../../../shared/models/stats.model';

@Component({
  selector: 'app-ngo-stats',
  imports: [],
  templateUrl: './ngo-stats.component.html',
  styleUrl: './ngo-stats.component.css'
})
export class NgoStatsComponent {

  @Input() stats!: NgoStats;

}
