import {AfterViewInit, Component, DestroyRef, ElementRef, inject, Input, ViewChild} from '@angular/core';
import * as L from 'leaflet';

@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.component.html',
  styleUrl: './map.component.css'
})
export class MapComponent implements AfterViewInit {

  @ViewChild('mapContainer') mapContainer!: ElementRef;
  private readonly destroyRef = inject(DestroyRef);

  @Input({ required: true }) pickupLat!: number;
  @Input({ required: true }) pickupLng!: number;
  @Input({ required: true }) dropoffLat!: number;
  @Input({ required: true }) dropoffLng!: number;

  private map: L.Map | undefined;

  ngAfterViewInit() {
    this.fixLeafletIcons();
    this.initMap();

    this.destroyRef.onDestroy(() => {
      if(this.map){
        this.map.remove();
      }
    })
  }

  private initMap(){
    //Inicializar el mapa en el contenedor HTML
    this.map = L.map(this.mapContainer.nativeElement);

    //Configurar la capa de "azulejos" (tiles) de OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    //Crear marcadores
    const pickupMarker = L.marker([this.pickupLat, this.pickupLng]).bindPopup('<b>Punto A</b><br>Comercio Donante');
    const dropoffMarker = L.marker([this.dropoffLat, this.dropoffLng]).bindPopup('<b>Punto B</b><br>Organización (ONG)');

    //Crear la línea de ruta (Polyline)
    const routeLine = L.polyline([
      [this.pickupLat, this.pickupLng],
      [this.dropoffLat, this.dropoffLng]
    ], {
      color: '#eb5c0c',
      weight: 4,
      opacity: 0.8,
      dashArray: '10, 10' //línea punteada
    });

    pickupMarker.addTo(this.map);
    dropoffMarker.addTo(this.map);
    routeLine.addTo(this.map);

    //Ajustar para que se vean ambos puntos
    const bounds = L.latLngBounds([
      [this.pickupLat, this.pickupLng],
      [this.dropoffLat, this.dropoffLng]
    ]);
    this.map.fitBounds(bounds, { padding: [50, 50] });
  }

  private fixLeafletIcons(){
    const iconRetinaUrl = 'assets/marker-icon-2x.png';
    const iconUrl = 'assets/marker-icon.png';
    const shadowUrl = 'assets/marker-shadow.png';
    const iconDefault = L.icon({
      iconRetinaUrl,
      iconUrl,
      shadowUrl,
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      tooltipAnchor: [16, -28],
      shadowSize: [41, 41]
    });
    L.Marker.prototype.options.icon = iconDefault;
  }


}
