import {Component, Input} from '@angular/core';
import {AdminStats} from '../../../shared/models/stats.model';

@Component({
  selector: 'app-admin-stats',
  imports: [],
  templateUrl: './admin-stats.component.html',
  styleUrl: './admin-stats.component.css'
})
export class AdminStatsComponent {
  @Input() stats!: AdminStats;
}
