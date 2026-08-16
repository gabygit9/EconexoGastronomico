import {Component, Input, ViewChild} from '@angular/core';
import {AdminStats} from '../../../shared/models/stats.model';
import {ChartComponent} from 'ng-apexcharts';
import {StatusTranslatePipe} from '../../../shared/pipes/status-translate.pipe';
import {CurrencyPipe, DecimalPipe} from '@angular/common';

@Component({
  selector: 'app-admin-stats',
  imports: [
    ChartComponent,
    DecimalPipe,
    CurrencyPipe
  ],
  templateUrl: './admin-stats.component.html',
  styleUrl: './admin-stats.component.css'
})
export class AdminStatsComponent {
  @ViewChild("chart") chart!: ChartComponent;
  private _stats!: AdminStats;

  public heatmapOptions: any;
  public funnelOptions: any;
  public treeMapOptions: any;
  public barOptions: any;
  public trendOptions: any;
  public gaugeOptions: any;
  public donutOptions: any;
  public topNgosOptions: any;

  @Input() set stats(value: AdminStats) {
    if (value) {
      this._stats = value;
      this.initCharts();
    }
  }

  get stats() { return this._stats; }

  hasComparison(): boolean {
    return !!this.stats?.['comparison'];
  }

  trend(key:string): number | null {
    const value = this.stats?.['comparison']?.[key];
    return value != undefined && value !== null ? value : null;
  }

  trendPrev(key: string): number | null {
    const value = this.stats?.['comparison']?.[key + 'Prev'];
    return value !== undefined && value !== null ? value : null;
  }

  displayTrend(value: number): string {
    const abs = Math.abs(value);
    if (abs > 300) {
      return (value >= 0 ? '+300' : '-300') + '%+';
    }
    return (value >= 0 ? '+' : '') + value.toFixed(1) + '%';
  }

  absValue(value: number): number {
    return Math.abs(value);
  }

  /**
   * Initialize all charts
   */
  initCharts() {
    this.heatmapOptions = this.heatmap();
    this.funnelOptions = this.funnel();
    this.treeMapOptions = this.treeMap();
    this.barOptions = this.bar();
    this.trendOptions = this.trend_chart();
    this.gaugeOptions = this.gauge();
    this.donutOptions = this.donutComposition();
    this.topNgosOptions = this.topNgosBar();
  }

  /**
   * Generate heatmap data
   */
  heatmap(){
    const heatmapData = this.stats['heatmap'] || [];
    const seriesData = [0,1,2,3,4,5,6].map(day => {
      return {
        name: ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'][day],
        data: [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23].map(hour => {
          const found = heatmapData.find((item:any) => Number(item[0]) === day && Number(item[1]) === hour);
          return { x: hour + ':00', y: found ? Number(found[2]) : 0 };
        })
      };
    });

    return {
      series: seriesData,
      chart: { type: 'heatmap', height: 350, width: '100%', toolbar: { show: false } },
      title: { text: 'Intensidad de Donaciones (Día/Hora)' },
      dataLabels: { enabled: true, style: { fontSize: '11px' } },
      plotOptions: {
        heatmap: {
          shadeIntensity: 0.5,
          colorScale: {
            ranges: [{ from: 0, to: 0, name: 'Sin actividad', color: '#94a3b8' },
              { from: 1, to: 5, name: 'Baja', color: '#059669' },
              { from: 6, to: 20, name: 'Alta', color: '#059669' }]
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

  /**
   * Generate funnel data
   */
  funnel(){
    const funnelData = this.stats['funnel'] || [];

    return {
      series: [{
        name: 'Donaciones',
        data: funnelData.map((item: any) => {
          const statusKey = String(item[0]).trim().toUpperCase();
          return {
            x: this.statusTranslations[statusKey] || item[0],
            y: item[1],
            fillColor: this.statusColors[statusKey] || '#94a3b8'
          };
        })
      }],
      chart: { type: 'bar', height: 350, width: '100%', toolbar: { show: false } },
      plotOptions: {
        bar: {
          horizontal: true,
          distributed: true,
          borderRadius: 4
        }
      },
      dataLabels: { enabled: true, style: { fontSize: '11px' } },
      legend: { show: false },
      title: { text: 'Donaciones por Estado' },
      responsie: [{
        breakpoint: 640,
        options: {
          title: { style: { fontsize: '14px' } },
          dataLabels: { style: { fontsiza: '9px' } }
        }
      }]
    };
  }

  /**
   * Generate tree map data
   */
  treeMap(){
    const treemapData = this.stats['treemap'] || [];

    return {
      series: [{
        data: treemapData.map((item: any) => ({ x: item[0], y: item[1] }))
      }],
      chart: { type: 'treemap', height: 350, width: '100%', toolbar: { show: false } },
      dataLabels: {
        enabled: true,
        style: { fontSize: '14px', fontWeight: 'bold'},
        formatter: function(text:string, op:any){
          return [text, op.value];
        }
      },
      colors: [ '#f59e0b', '#ef4444', '#059669', '#3b82f6'],
      plotOptions: {
        treeMap: {
          distributed: true,
          enabledShades: false
        }
      },
      title: { text: 'Volumen por Categoría' },
      responsive: [{
        breakpoint: 640,
        options: {
          title: { style: { fontSize: '14px' } },
          dataLabels: { style: { fontSize: '10px' } }
        }
      }]
    };
  }

  /**
   * Generate bar data
   */
  bar(){
    const topDriversData = this.stats['topDrivers'] || [];

    return {
      series: [{
        name: 'Entregas',
        data: topDriversData.map((d: any) => d[1])
      }],
      chart: { type: 'bar', height: 350, width: '100%', toolbar: { show: false } },
      colors: ['#6366f1'],
      plotOptions: {
        bar: {
          borderRadius: 8,
          columnWidth: '30%',
          distributed: true
        }
      },
      legend: { show: false },
      dataLabels: { enabled: true, offsetY: -20, style: { fontSize: '12px' } },
      tooltip: { y: { formatter: (val: number) => `${val} entregas exitosas` } },
      yaxis: { labels: { formatter: (val: number) => Math.floor(val) } },
      xaxis: { categories: topDriversData.map((d: any) => 'Conductor ' + d[0]),
        labels: { rotate: 0, style: { fontSize: '11px' } } },
      title: { text: 'Ranking Top 5 Conductores' },
      responsive: [{
        breakpoint: 640,
        options: {
          title: { style: { fontSize: '14px' } },
          xaxis: { labels: { style: { fontSize: '9px' } } }
        }
      }]
    };
  }

  /**
   * Generate top NGOs data
   */
  topNgosBar(){
    const data = this.stats['topNgos'] || [];

    return {
      series: [{ name: 'Kilos recibidos', data: data.map((d: any) => Number(d[2])) }],
      chart: { type: 'bar', height: 350, width: '100%', toolbar: { show: false } },
      plotOptions: { bar: { horizontal: true, borderRadius: 4 } },
      colors: ['#0891b2'],
      dataLabels: { enabled: true, style: { fontSize: '11px' } },
      xaxis: {
        categories: data.map((d: any) => d[1]),
        labels: { style: { fontSize: '11px' } }
      },
      title: { text: 'Top 5 ONGs Receptoras (kg)' },
      responsive: [{
        breakpoint: 640,
        options: {
          title: { style: { fontSize: '14px' } },
          dataLabels: { style: { fontSize: '9px' } }
        }
      }]
    };
  }

  /**
   * Generate donut composition data
   */
  donutComposition(){
    return {
      series: [this.stats['totalNgos'], this.stats['totalDonors'], this.stats['totalDrivers']],
      labels: ['ONGs', 'Donantes', 'Conductores'],
      chart: { type: 'donut', height: 320 },
      colors: ['#3b82f6', '#eb5c0c', '#059669'],
      dataLabels: { enabled: true, style: { fontSize: '12px' }, dropShadow: { enabled: false } },
      legend: { position: 'bottom', fontSize: '12px' },
      title: { text: 'Composición de la Red' },
      responsive: [{
        breakpoint: 640,
        options: {
          chart: { height: 280 },
          title: { style: { fontSize: '14px' } },
          dataLabels: { style: { fontSize: '9px' } },
          legend: { fontSize: '10px' }
        }
      }]
    };
  }

  /**
   * Generate gauge data
   */
  gauge(){
    const value = Math.round(this.stats['networkPunctuality'] || 0);
    const color = value >= 80 ? '#059669' : value >= 50 ? '#f59e0b' : '#ef4444';

    return {
      series: [value],
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
      fill: { colors: [color] },
      title: { text: 'Puntualidad de Red' },
      subtitle: { text: 'Entregas a tiempo, período seleccionado', style: { color: '#9ca3af' } }
    };
  }

  /**
   * Generate trend chart data
   */
  trend_chart(){
    const trendData = this.stats['monthlyTrend'] || [];
    const monthNames = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];

    const categories = trendData.map((item: any) => `${monthNames[Number(item[1]) - 1]} ${item[0]}`);
    const deliveries = trendData.map((item: any) => Number(item[2]));
    const kilos = trendData.map((item: any) => Number(item[3]));

    return {
      series: [
        { name: 'Entregas', data: deliveries },
        { name: 'Kilos', data: kilos }
      ],
      chart: { type: 'line', height: 350, width: '100%', toolbar: { show: false }, zoom: { enabled: false } },
      stroke: { curve: 'smooth', width: 3 },
      colors: ['#eb5c0c', '#059669'],
      markers: { size: 5 },
      xaxis: { categories, labels: { rotate: 0, style: { fontSize: '11px' } } },
      title: { text: 'Tendencia de la Red (Entregas y Kilos por Mes)' },
      responsive: [{
        breakpoint: 640,
        options: {
          title: { style: { fontSize: '14px' } },
          xaxis: { labels: { style: { fontSize: '9px' } } },
          markers: { size: 3 }
        }
      }]
    };
  }

  /**
   * Status colors helper
   */
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

  /**
   * Status translations helper
   */
  private statusTranslations: { [key: string]: string } = {
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
