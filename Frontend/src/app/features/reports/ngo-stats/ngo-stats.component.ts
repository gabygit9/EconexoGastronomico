import {Component, inject, Input, ViewChild} from '@angular/core';
import {NgoStats} from '../../../shared/models/stats.model';
import {ChartComponent, NgApexchartsModule} from 'ng-apexcharts';
import {CurrencyPipe, DatePipe, DecimalPipe, PercentPipe} from '@angular/common';
import {StatusTranslatePipe} from '../../../shared/pipes/status-translate.pipe';
import {DonationStatusColorsService} from '../../../core/services/donation-status-colors.service';

@Component({
  selector: 'app-ngo-stats',
  imports: [NgApexchartsModule, PercentPipe, DecimalPipe, DatePipe, CurrencyPipe],
  templateUrl: './ngo-stats.component.html',
  styleUrl: './ngo-stats.component.css'
})
export class NgoStatsComponent {
  private readonly statusTranslate = new StatusTranslatePipe();
  private readonly statusColors = inject(DonationStatusColorsService);

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
  public gaugeOptions: any;
  public funnelOptions: any;
  public topBusinessesOptions: any;

  get stats() { return this._stats; }

  private readonly MONTH_NAMES = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];

  hasComparison(): boolean {
    return !!this.stats?.comparison;
  }

  trend(key: keyof NonNullable<NgoStats['comparison']>): number | null {
    const value = this.stats?.comparison?.[key];
    return value !== undefined && value !== null ? value as number : null;
  }

  absValue(value: number): number {
    return Math.abs(value);
  }

  displayTrend(value: number): string {
    const abs = Math.abs(value);
    if (abs > 300) {
      return (value >= 0 ? '+300' : '-300') + '%+';
    }
    return (value >= 0 ? '+' : '') + value.toFixed(1) + '%';
  }

  initChart(){
    this.chartOpt();
    this.donutsOpt();
    this.gaugeOpt();
    this.funnelOpt();
    this.topBusinessesOpt();
  }

  private chartOpt(){
    const trendData = this.stats.monthlyTrend || [];
    const categories = trendData.map(t => `${this.MONTH_NAMES[t.month - 1]} ${t.year}`);

    this.chartOptions = {
      series: [
        { name: 'Kilos', type: 'column', data: trendData.map(t => t.kilos) },
        { name: 'Dinero', type: 'area', data: trendData.map(t => t.money) }
      ],
      chart: { height: 350, width: '100%', toolbar: { show: false }, zoom: { enabled: false } },
      stroke: { curve: 'smooth', width: [0, 3] },
      fill: { opacity: [0.9, 0.25] },
      colors: ['#059669', '#eb5c0c'],
      markers: { size: [0, 4] },
      xaxis: { categories, labels: { rotate: 0, style: { fontSize: '11px' } } },
      yaxis: [ { title: { text: 'Kilos' } }, { opposite: true, title: { text: 'Dinero ($)' } } ],
      title: { text: 'Evolución: Kilos y Dinero Recibido' },
      tooltip: { shared: true, intersect: false },
      responsive: [{
        breakpoint: 640,
        options: { title: { style: { fontSize: '14px' } }, xaxis: { labels: { style: { fontSize: '9px' } } } }
      }]
    };
  }

  private donutsOpt(){
    this.donutOptions = {
      series: this.stats.topCategories.map(category => category.quantity),
      chart: { type: 'donut', height: 320 },
      labels: this.stats.topCategories.map(category => category.categoryName),
      title: { text: 'Distribución por Categoría' },
      dataLabels: { enabled: true, style: { fontSize: '12px' }, dropShadow: { enabled: false } },
      legend: { position: 'right', fontSize: '13px' },
      responsive: [{
        breakpoint: 640,
        options: {
          chart: { height: 280 },
          title: { style: { fontSize: '14px' } },
          dataLabels: { enabled: true, style: { fontSize: '9px' } },
          legend: { position: 'bottom', fontSize: '11px' }
        }
      }]
    };
  }

  private gaugeOpt() {
    const rate = Math.round((this.stats.efficiencyRatio || 0) * 100);
    const gaugeColor = rate >= 80 ? '#059669' : rate >= 50 ? '#f59e0b' : '#ef4444';

    this.gaugeOptions = {
      series: [rate],
      chart: { type: 'radialBar', height: 320 },
      labels: [' '],
      plotOptions: {
        radialBar: {
          hollow: { size: '60%' },
          dataLabels: {
            name: { show: false, formatter: () => '', fontSize: '0px', color: 'transparent', offsetY: -9999 },
            value: { show: true, fontSize: '26px', fontWeight: 700, formatter: (val: number) => val + '%' }
          }
        }
      },
      fill: { colors: [gaugeColor] },
      title: { text: 'Eficiencia de Logística' },
      subtitle: { text: 'Kilos recibidos / solicitados', style: { color: '#9ca3af' } }
    };
  }

  private funnelOpt() {
    const funnelData = this.stats.funnel || [];
    this.funnelOptions = {
      series: [{
        name: 'Solicitudes',
        data: funnelData.map((item: any) => {
          return {
            x: this.statusTranslate.transform(item[0]),
            y: item[1],
            fillColor: this.statusColors.getHexColor(item[0]) };
        })
      }],
      chart: { type: 'bar', height: 320, width: '100%', toolbar: { show: false } },
      plotOptions: { bar: { horizontal: true, distributed: true, borderRadius: 4 } },
      dataLabels: { enabled: true, style: { fontSize: '11px' } },
      legend: { show: false },
      title: { text: 'Mis Solicitudes por Estado' },
      responsive: [{
        breakpoint: 640,
        options: { title: { style: { fontSize: '14px' } }, dataLabels: { style: { fontSize: '9px' } } }
      }]
    };

  }

  private topBusinessesOpt() {
    const topBusinessesData = this.stats.topBusinesses || [];
    this.topBusinessesOptions = {
      series: [{ name: 'Kilos donados', data: topBusinessesData.map(d => d.kilos) }],
      chart: { type: 'bar', height: 320, width: '100%', toolbar: { show: false } },
      plotOptions: { bar: { horizontal: true, borderRadius: 4 } },
      colors: ['#0891b2'],
      dataLabels: { enabled: true, style: { fontSize: '11px' } },
      xaxis: { categories: topBusinessesData.map(d => d.businessName), labels: { style: { fontSize: '11px' } } },
      title: { text: 'Comercios Donantes Frecuentes' },
      responsive: [{
        breakpoint: 640,
        options: { title: { style: { fontSize: '14px' } }, dataLabels: { style: { fontSize: '9px' } } }
      }]
    };
  }
}
