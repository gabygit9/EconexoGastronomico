import {Component, Input, ViewChild} from '@angular/core';
import {DriverStats} from '../../../shared/models/stats.model';
import {ChartComponent, NgApexchartsModule} from 'ng-apexcharts';
import {DecimalPipe, PercentPipe} from '@angular/common';

@Component({
  selector: 'app-driver-stats',
  imports: [NgApexchartsModule, DecimalPipe, PercentPipe],
  templateUrl: './driver-stats.component.html',
  styleUrl: './driver-stats.component.css'
})
export class DriverStatsComponent {
  @ViewChild("chart") chart!: ChartComponent;

  @Input() set stats(value: DriverStats){
    if(value){
      this._stats = value;
      console.log(this.stats);
      this.initChart();
    }
  }

  private _stats!: DriverStats;
  public activityOptions: any;
  public trendOptions: any;
  public lineOptions: any;

  get stats() { return this._stats; }

  private readonly MONTH_NAMES = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];

  initChart() {
    this.activityOptions = this.buildActivityChart();
    this.trendOptions = this.buildTrendChart();
    this.lineOptions = this.buildMonthlyPunctualityChart();
  }

  private buildActivityChart(){
    return {
      series: [{ name: 'Entregas', data: this.stats.activityByHour }],
      chart: { type: 'bar', height: 350, width: '100%', toolbar: { show: false } },
      title: { text: 'Distribución de Actividad (00-23hs)' },
      dataLabels: { enabled: true, style: { fontSize: '11px' } },
      xaxis: { categories: Array.from({ length: 24 }, (_, i) => i + ':00') },
      colors: ['#3b82f6'],
      responsive: [{
        breakpoint: 640,
        options: {
          dataLabels: { enabled: false },
          title: { style: { fontSize: '13px' } },
          xaxis: {
            labels: {
              rotate: 0,
              style: { fontSize: '9px' },
              formatter: (value: string) => {
                const hour = parseInt(value, 10);
                return hour % 3 === 0 ? value : '';
              }
            }
          }
        }
      }]
    };
  }

  private buildTrendChart(){
    const roundedPunctuality = Math.round(this.stats.punctualityPercentage);

    return {
      series: [ roundedPunctuality ],
      chart: { type: 'radialBar', height: 350 },
      labels: [' '],
      plotOptions: {
        radialBar: {
          dataLabels: {
            name: { show: false, formatter: () => '', fontSize: '0px', color: 'transparent', offsetY: -9999 },
            value: {
              show: true,
              fontSize: '22px',
              formatter: (val: number) => val + '%' }
          }
        }
      },
      title: { text: 'Indice de Eficiencia Operativa' },
      subtitle: { text: 'Entregas realizadas a tiempo', style: { color: '#9ca3af' } }
    };
  }

  private buildMonthlyPunctualityChart(){
    const rawData: any[] = this.stats.monthlyPunctuality || [];
    const valuesByMonth = new Map<number, number>(rawData.map(d => [d.month, d.value]));
    const values = this.MONTH_NAMES.map((_, idx) => valuesByMonth.get(idx + 1) ?? 0);

    return {
      series: [{
        name: 'Puntualidad (%)',
        data: values
      }],
      chart: {
        type: 'line',
        height: 350,
        width: '100%',
        toolbar: { show: false },
        zoom: { enabled: false }
      },
      stroke: { curve: 'smooth', width: 3 },
      xaxis: {
        categories: this.MONTH_NAMES,
        title: { text: 'Meses' },
        labels: { rotate: 0, style: { fontSize: '11px' } }
      },
      yaxis: { min: 0, max: 100, labels: { formatter: (val: number) => Math.round(val).toString() } },
      title: {
        text: 'Tendencia de Puntualidad Mensual',
        align: 'center'
      },
      colors: ['#059669'],
      markers: {
        size: 6,
        strokeWidth: 2,
        hover: { size: 8}
      },
      responsive: [{
        breakpoint: 640,
        options: {
          title: { style: { fontSize: '13px' } },
          xaxis: { labels: { style: { fontSize: '9px' } } },
          markers: { size: 4 }
        }
      }]
    };
  }
}
