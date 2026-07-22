import {Component, Input, ViewChild} from '@angular/core';
import {DonorStats} from '../../../shared/models/stats.model';
import {CurrencyPipe, DatePipe, DecimalPipe} from '@angular/common';
import {ChartComponent, NgApexchartsModule} from 'ng-apexcharts';

@Component({
  selector: 'app-donor-stats',
  imports: [
    DecimalPipe,
    CurrencyPipe,
    ChartComponent,
    DatePipe,
    NgApexchartsModule
  ],
  templateUrl: './donor-stats.component.html',
  styleUrl: './donor-stats.component.css'
})
export class DonorStatsComponent {
  @ViewChild("chart") chart!: ChartComponent;
  private _stats!: DonorStats;
  public chartOptions: any;
  public donutOptions: any;

  @Input() set stats(value: DonorStats) {
    if (value) {
      this._stats = value;
      this.initCharts();
    }
  }

  get stats() { return this._stats; }

  initCharts() {
    const prevRations = (this.stats.prevMonthImpact || 0) * 2;
    const currRations = (this.stats.currentMonthImpact || 0) * 2;

    this.chartOptions = {
      series: [
        { name: 'Kilos (kg)', data: [this.stats.prevMonthImpact, this.stats.currentMonthImpact] },
        { name: 'Dinero ($)', data: [this.stats.prevMoney, this.stats.currentMoney] },
        { name: 'Raciones Est. (Impacto)', data: [prevRations, currRations] }
      ],
      chart: { type: 'area', height: 350, toolbar: { show: false }  },
      stroke: { curve: 'smooth' },
      title: { text: 'Evolución: Impacto, Aporte Monetario y Raciones Estimadas' },
      tooltip: { shared: true, intersect: false },
      xaxis: { categories: ['Mes Anterior', 'Mes Actual'] }
    };

    this.donutOptions = {
      series: this.stats.topCategories.map(c => c.quantity),
      labels: this.stats.topCategories.map(c => c.categoryName),
      chart: { type: 'donut', height: 350 },
      title: { text: 'Distribución de Donaciones' },
      dataLabels: {
        enabled: true,
        style: { fontSize: '12px' },
        dropShadow: { enabled: false }
      },
      legend: {
        position: 'right',
        fontSize: '13px'
      },
      responsive: [{
        breakpoint: 640,
        options: {
          chart: { height: 300 },
          dataLabels: {
            enabled: true,
            style: { fontSize: '9px' }
          },
          legend: {
            position: 'bottom',
            fontSize: '11px'
          }
        }
      }]
    };
  }
}
