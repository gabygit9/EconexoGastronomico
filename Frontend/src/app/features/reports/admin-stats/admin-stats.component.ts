import {Component, Input, ViewChild} from '@angular/core';
import {AdminStats} from '../../../shared/models/stats.model';
import {ChartComponent} from 'ng-apexcharts';
import {StatusTranslatePipe} from '../../../shared/pipes/status-translate.pipe';

@Component({
  selector: 'app-admin-stats',
  imports: [
    ChartComponent
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

  @Input() set stats(value: AdminStats) {
    if (value) {
      this._stats = value;
      this.initCharts();
    }
  }

  get stats() { return this._stats; }

  initCharts() {
    this.heatmapOptions = this.heatmap();
    this.funnelOptions = this.funnel();
    this.treeMapOptions = this.treeMap();
    this.barOptions = this.bar();
  }

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
      chart: { type: 'bar', height: 350 },
      plotOptions: {
        bar: {
          horizontal: true,
          distributed: true,
          borderRadius: 4
        }
      },
      legend: { show: false },
      title: { text: 'Donaciones por Estado' }
    };
  }

  treeMap(){
    const treemapData = this.stats['treemap'] || [];

    return {
      series: [{
        data: treemapData.map((item: any) => ({ x: item[0], y: item[1] }))
      }],
      chart: { type: 'treemap', height: 350 },
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
      title: { text: 'Volumen por Categoría' }
    };
  }

  bar(){
    const topDriversData = this.stats['topDrivers'] || [];

    return {
      series: [{
        name: 'Entregas',
        data: topDriversData.map((d: any) => d[1])
      }],
      chart: { type: 'bar', height: 350 },
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
      xaxis: { categories: topDriversData.map((d: any) => 'Conductor ' + d[0]) },
      title: { text: 'Ranking Top 5 Conductores' }
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
