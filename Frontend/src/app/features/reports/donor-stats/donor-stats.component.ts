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
  public gaugeOptions: any;
  public topNgosOptions: any;
  public funnelOptions: any;
  public heatmapOptions: any;

  @Input() set stats(value: DonorStats) {
    if (value) {
      this._stats = value;
      this.initCharts();
    }
  }

  get stats() { return this._stats; }

  hasComparison(): boolean {
    return !!this.stats?.comparison;
  }

  trend(key: keyof NonNullable<DonorStats['comparison']>): number | null {
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

  initCharts() {
    this.chartOptions = this.chartOpt();
    this.donutOptions = this.donut();
    this.gaugeOptions = this.gauge();
    this.topNgosOptions = this.topNgos();
    this.buildFunnel();
    this.buildHeatmap();
  }

  private chartOpt(){
    const trendData = this.stats.monthlyTrend || [];
    const monthNames = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
    const categories = trendData.map(t => `${monthNames[t.month - 1]} ${t.year}`);

    return {
      series: [
        { name: 'Kilos', type: 'column', data: trendData.map(t => t.kilos) },
        { name: 'Dinero', type: 'area', data: trendData.map(t => t.money) }
      ],
      chart: { height: 350, width: '100%', toolbar: { show: false }, zoom: { enabled: false } },
      stroke: { curve: 'smooth', width: [0, 3] },
      fill: { opacity: [0.9, 0.25] },
      colors: ['#eb5c0c', '#059669'],
      markers: { size: [0, 4] },
      xaxis: { categories, labels: { rotate: 0, style: { fontSize: '11px' } } },
      yaxis: [
        { title: { text: 'Kilos' } },
        { opposite: true, title: { text: 'Dinero ($)' } }
      ],
      title: { text: 'Evolución: Kilos y Dinero Donado' },
      tooltip: { shared: true, intersect: false },
      responsive: [{
        breakpoint: 640,
        options: {
          title: { style: { fontSize: '14px' } },
          xaxis: { labels: { style: { fontSize: '9px' } } }
        }
      }]
    };
  }

  private donut(){
    return {
      series: this.stats.topCategories.map(c => c.quantity),
      labels: this.stats.topCategories.map(c => c.categoryName),
      chart: { type: 'donut', height: 320 },
      title: { text: 'Distribución de Donaciones' },
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

  private gauge(){
    const rate = Math.round(this.stats.successRate || 0);
    const gaugeColor = rate >= 80 ? '#059669' : rate >= 50 ? '#f59e0b' : '#ef4444';

    return  {
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
      title: { text: 'Tasa de Éxito' },
      subtitle: { text: 'Donaciones entregadas / total', style: { color: '#9ca3af' } }
    };
  }

  private topNgos(){
    const topNgosData = this.stats.topNgos || [];
    return {
      series: [{ name: 'Kilos donados', data: topNgosData.map(n => n.kilos) }],
      chart: { type: 'bar', height: 320, width: '100%', toolbar: { show: false } },
      plotOptions: { bar: { horizontal: true, borderRadius: 4 } },
      colors: ['#0891b2'],
      dataLabels: { enabled: true, style: { fontSize: '11px' } },
      xaxis: {
        categories: topNgosData.map(n => n.ngoName),
        labels: { style: { fontSize: '11px' } }
      },
      title: { text: 'ONGs Beneficiadas' },
      responsive: [{
        breakpoint: 640,
        options: {
          title: { style: { fontSize: '14px' } },
          dataLabels: { style: { fontSize: '9px' } }
        }
      }]
    };
  }

  private buildFunnel(){
    const funnelData = this.stats.funnel || [];
    this.funnelOptions = {
      series: [{
        name: 'Publicaciones',
        data: funnelData.map((item: any) => {
          const statusKey = String(item[0]).trim().toUpperCase();
          return {
            x: this.statusTranslations[statusKey] || item[0],
            y: item[1],
            fillColor: this.statusColors[statusKey] || '#94a3b8'
          };
        })
      }],
      chart: { type: 'bar', height: 320, width: '100%', toolbar: { show: false } },
      plotOptions: { bar: { horizontal: true, distributed: true, borderRadius: 4 } },
      dataLabels: { enabled: true, style: { fontSize: '11px' } },
      legend: { show: false },
      title: { text: 'Mis Publicaciones por Estado' },
      responsive: [{
        breakpoint: 640,
        options: { title: { style: { fontSize: '14px' } }, dataLabels: { style: { fontSize: '9px' } } }
      }]
    };
  }

  private buildHeatmap(){
    const heatmapData = this.stats.heatmap || [];
    const seriesData = [0,1,2,3,4,5,6].map(day => ({
      name: ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'][day],
      data: [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23].map(hour => {
        const found = heatmapData.find((item:any) => Number(item[0]) === day && Number(item[1]) === hour);
        return { x: hour + ':00', y: found ? Number(found[2]) : 0 };
      })
    }));

    this.heatmapOptions = {
      series: seriesData,
      chart: { type: 'heatmap', height: 320, width: '100%', toolbar: { show: false } },
      title: { text: 'Mi Actividad de Publicación (Día/Hora)' },
      dataLabels: { enabled: true, style: { fontSize: '11px' } },
      plotOptions: {
        heatmap: {
          shadeIntensity: 0.5,
          colorScale: {
            ranges: [{ from: 0, to: 0, name: 'Sin actividad', color: '#94a3b8' },
              { from: 1, to: 5, name: 'Baja', color: '#eb5c0c' },
              { from: 6, to: 20, name: 'Alta', color: '#eb5c0c' }]
          }
        }
      },
      responsive: [{
        breakpoint: 640,
        options: {
          dataLabels: { enabled: false },
          title: { style: { fontSize: '14px' } },
          xaxis: {
            labels: {
              rotate: 0, style: { fontSize: '9px' },
              formatter: (value: string) => { const hour = parseInt(value, 10); return hour % 3 === 0 ? value : ''; }
            }
          }
        }
      }]
    };
  }

  private statusColors: { [key: string]: string } = {
    'PENDING_PAYMENT': '#f59e0b',
    'AVAILABLE': '#6366f1',
    'REQUESTED': '#3b82f6',
    'ASSIGNED': '#8b5cf6',
    'IN_TRANSIT': '#0ea5e9',
    'DELIVERED_PENDING_NGO': '#fbbf24',
    'DELIVERED': '#059669',
    'REJECTED': '#ef4444',
    'CANCELED': '#64748b',
    'EXPIRED': '#94a3b8',
    'COMPLETED': '#10b981'
  };

  public statusTranslations: { [key: string]: string } = {
    'PENDING_PAYMENT': "Pendiente",
    'AVAILABLE': 'Disponible',
    'REQUESTED': 'Solicitado',
    'ASSIGNED': 'Asignado',
    'IN_TRANSIT': 'En tránsito',
    'DELIVERED_PENDING_NGO': 'En destino',
    'DELIVERED': 'Entregado',
    'REJECTED': 'Rechazado',
    'CANCELED': 'Cancelado',
    'EXPIRED': 'Expirado',
    'COMPLETED': 'Completado'
  };

}
