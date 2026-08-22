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
      this.initChart();
    }
  }

  private _stats!: DriverStats;
  public activityOptions: any;
  public trendOptions: any;
  public lineOptions: any;
  public funnelOptions: any;
  public topBusinessesOptions: any;
  public topNgosOptions: any;
  public monthlyTrendOptions: any;

  get stats() { return this._stats; }

  private readonly MONTH_NAMES = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];

  private statusColors: { [key: string]: string } = {
    'PENDING_PAYMENT': '#f59e0b', 'AVAILABLE': '#6366f1', 'REQUESTED': '#3b82f6',
    'ASSIGNED': '#8b5cf6', 'IN_TRANSIT': '#0ea5e9', 'DELIVERED_PENDING_NGO': '#fbbf24',
    'DELIVERED': '#059669', 'REJECTED': '#ef4444', 'CANCELED': '#64748b',
    'EXPIRED': '#94a3b8', 'COMPLETED': '#10b981'
  };

  private statusTranslations: { [key: string]: string } = {
    'PENDING_PAYMENT': "Pendiente", 'AVAILABLE': 'Disponible', 'REQUESTED': 'Solicitado',
    'ASSIGNED': 'Asignado', 'IN_TRANSIT': 'En tránsito', 'DELIVERED_PENDING_NGO': 'En destino',
    'DELIVERED': 'Entregado', 'REJECTED': 'Rechazado', 'CANCELED': 'Cancelado',
    'EXPIRED': 'Expirado', 'COMPLETED': 'Completado'
  };

  hasComparison(): boolean {
    return !!this.stats?.comparison;
  }

  trend(key: keyof NonNullable<DriverStats['comparison']>): number | null {
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

  initChart() {
    this.activityOptions = this.buildActivityChart();
    this.trendOptions = this.buildTrendChart();
    this.lineOptions = this.buildMonthlyPunctualityChart();
    this.funnelOptions = this.buildFunnelChart();
    this.topBusinessesOptions = this.buildTopBusinessesChart();
    this.topNgosOptions = this.buildTopNgosChart();
    this.monthlyTrendOptions = this.buildMonthlyTrendChart();
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

  private buildFunnelChart(){
    const funnelData = this.stats.funnel || [];
    return {
      series: [{
        name: 'Viajes',
        data: funnelData.map((item: any) => {
          const statusKey = String(item[0]).trim().toUpperCase();
          return { x: this.statusTranslations[statusKey] || item[0], y: item[1], fillColor: this.statusColors[statusKey] || '#94a3b8' };
        })
      }],
      chart: { type: 'bar', height: 320, width: '100%', toolbar: { show: false } },
      plotOptions: { bar: { horizontal: true, distributed: true, borderRadius: 4 } },
      dataLabels: { enabled: true, style: { fontSize: '11px' } },
      legend: { show: false },
      title: { text: 'Mis Viajes por Estado' },
      responsive: [{
        breakpoint: 640,
        options: { title: { style: { fontSize: '14px' } }, dataLabels: { style: { fontSize: '9px' } } }
      }]
    };
  }

  private buildTopBusinessesChart(){
    const data = this.stats.topBusinesses || [];
    return {
      series: [{ name: 'Kilos retirados', data: data.map(d => d.kilos) }],
      chart: { type: 'bar', height: 320, width: '100%', toolbar: { show: false } },
      plotOptions: { bar: { horizontal: true, borderRadius: 4 } },
      colors: ['#eb5c0c'],
      dataLabels: { enabled: true, style: { fontSize: '11px' } },
      xaxis: { categories: data.map(d => d.businessName), labels: { style: { fontSize: '11px' } } },
      title: { text: 'Comercios de Retiro Frecuentes' },
      responsive: [{
        breakpoint: 640,
        options: { title: { style: { fontSize: '14px' } }, dataLabels: { style: { fontSize: '9px' } } }
      }]
    };
  }

  private buildTopNgosChart(){
    const data = this.stats.topNgos || [];
    return {
      series: [{ name: 'Kilos entregados', data: data.map(d => d.kilos) }],
      chart: { type: 'bar', height: 320, width: '100%', toolbar: { show: false } },
      plotOptions: { bar: { horizontal: true, borderRadius: 4 } },
      colors: ['#0891b2'],
      dataLabels: { enabled: true, style: { fontSize: '11px' } },
      xaxis: { categories: data.map(d => d.ngoName), labels: { style: { fontSize: '11px' } } },
      title: { text: 'ONGs de Entrega Frecuentes' },
      responsive: [{
        breakpoint: 640,
        options: { title: { style: { fontSize: '14px' } }, dataLabels: { style: { fontSize: '9px' } } }
      }]
    };
  }

  private buildMonthlyTrendChart(){
    const trendData = this.stats.monthlyTrend || [];
    const categories = trendData.map(t => `${this.MONTH_NAMES[t.month - 1]} ${t.year}`);

    return {
      series: [
        { name: 'Entregas', type: 'column', data: trendData.map(t => t.deliveries) },
        { name: 'Kilos', type: 'area', data: trendData.map(t => t.kilos) }
      ],
      chart: { height: 320, width: '100%', toolbar: { show: false }, zoom: { enabled: false } },
      stroke: { curve: 'smooth', width: [0, 3] },
      fill: { opacity: [0.9, 0.25] },
      colors: ['#3b82f6', '#059669'],
      markers: { size: [0, 4] },
      xaxis: { categories, labels: { rotate: 0, style: { fontSize: '11px' } } },
      yaxis: [ { title: { text: 'Entregas' } }, { opposite: true, title: { text: 'Kilos' } } ],
      title: { text: 'Tendencia de Entregas y Kilos' },
      tooltip: { shared: true, intersect: false },
      responsive: [{
        breakpoint: 640,
        options: { title: { style: { fontSize: '14px' } }, xaxis: { labels: { style: { fontSize: '9px' } } } }
      }]
    };
  }

}
