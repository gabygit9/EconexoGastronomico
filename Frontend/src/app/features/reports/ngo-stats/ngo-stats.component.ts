import {Component, Input, ViewChild} from '@angular/core';
import {NgoStats} from '../../../shared/models/stats.model';
import {ChartComponent, NgApexchartsModule} from 'ng-apexcharts';
import {CurrencyPipe, DatePipe, DecimalPipe, PercentPipe} from '@angular/common';

@Component({
  selector: 'app-ngo-stats',
  imports: [NgApexchartsModule, PercentPipe, DecimalPipe, DatePipe, CurrencyPipe],
  templateUrl: './ngo-stats.component.html',
  styleUrl: './ngo-stats.component.css'
})
export class NgoStatsComponent {

  @ViewChild("chart") chart!: ChartComponent;

  @Input() set stats(value: NgoStats){
    if(value){
      this._stats = value;
      this.initChart();
    }
  }

  private _stats!: NgoStats;
  public chartOptions: any;
  public donutOptions: any;

  get stats() { return this._stats; }

  initChart(){
    this.chartOptions = {
      series: [{
        name: 'Kilos',
        data: [this.stats.prevMonthImpact, this.stats.monthlyImpact]
      }],
      chart: { type: 'bar', height: 350 },
      title: { text: 'Impacto Mensual (kg)' },
      xaxis: { categories: ['Mes Anterior', 'Mes Actual'] },
      colors: ['#059669']
    };

    this.donutOptions = {
      series: this.stats.topCategories.map(category => category.quantity),
      chart: { type: 'donut', height: 350 },
      labels: this.stats.topCategories.map(category => category.categoryName),
      title: { text: 'Distribución por Categoría' }
    }
  }
}
