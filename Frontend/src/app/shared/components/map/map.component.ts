import {AfterViewInit, Component, DestroyRef, ElementRef, inject, Input, ViewChild} from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';

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
  @Input({ required: true }) dropOffLat!: number;
  @Input({ required: true }) dropOffLng!: number;

  private _status: string = '';
  @Input({ required: true }) set tripStatus(value: string){
    this._status = value;
    this.updateMapRoute();
  }

  private map: L.Map | undefined;
  private routingControl: any;
  private currentDriverLat: number | null = null;
  private currentDriverLng: number | null = null;

  private watchId: number | null = null;
  private driverMarker: L.Marker | undefined;

  ngAfterViewInit() {
    this.fixLeafletIcons();
    this.initMap();
    this.trackDriverLocation();

    this.destroyRef.onDestroy(() => {
      if(this.watchId !== null){
        navigator.geolocation.clearWatch(this.watchId);
      }
      if(this.routingControl && this.map){
        this.map.removeControl(this.routingControl);
      }
      if(this.map){
        this.map.remove();
      }
    })
  }

  private initMap(){
    //Inicializar el mapa en el contenedor HTML
    this.map = L.map(this.mapContainer.nativeElement);

    this.map.createPane('driverPane');
    this.map.getPane('driverPane')!.style.zIndex = '1000';

    //Configurar la capa de "azulejos" (tiles) de OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    this.routingControl = L.Routing.control({
      plan: L.Routing.plan(
        [
          L.latLng(this.pickupLat, this.pickupLng),
          L.latLng(this.dropOffLat, this.dropOffLng),
        ],
        {
          createMarker: (i, wp, n) => {
            const marker = L.marker(wp.latLng);
            if(i === 0) marker.bindPopup('<b>Punto A</b><br>Retiro');
            if(i === n-1) marker.bindPopup('<b>Punto B</b><br>Entrega');
            return marker;
          }
        }
      ),
      lineOptions: {
        styles: [{color: '#eb5c0c', opacity: 0.8, weight: 6}],
        extendToWaypoints: false,
        missingRouteTolerance: 0
      },
      addWaypoints: false,
      draggableWaypoints: false,
      router: L.Routing.osrmv1({
        serviceUrl: `https://router.project-osrm.org/route/v1`
      }),
      show: false,
      collapsible: false
    }as any).addTo(this.map);

    this.updateMapRoute();
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

  private trackDriverLocation(){
    if(!navigator.geolocation){
      console.warn("The browser doesn't allow geolocalization ");
      return;
    }

    this.watchId = navigator.geolocation.watchPosition(
      (position) => {
        const lat = position.coords.latitude;
        const lng = position.coords.longitude;

        this.currentDriverLat = lat;
        this.currentDriverLng = lng;

        if(!this.driverMarker && this.map){
          const radarHtml = `
            <div class="driver-radar-pulsing">
                <div class="dot"></div>
            </div>
          `;

          const driverDivIcon = L.divIcon({
            html: radarHtml,
            iconSize: [32, 32],
            iconAnchor: [16, 16],
            className: ''
          });

          this.driverMarker = new L.Marker([lat, lng], {
            icon: driverDivIcon,
            pane: 'driverPane'
          }).addTo(this.map).bindPopup('<b>Tu ubicación actual</b>');

        }else if (this.driverMarker){
          this.driverMarker.setLatLng([lat, lng]);
        }

        if(this._status === 'ASSIGNED' || this._status === 'IN_TRANSIT'){
          this.updateMapRoute();
        }
      },
      (error) => {
        console.warn("Error getting location in real time.", error);
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 5000
      }
    )
  }

  private updateMapRoute(){
    if(!this.routingControl) return;

    let startPoint: L.LatLng;
    let endPoint: L.LatLng;

    if(this._status === 'ASSIGNED'){
      //Ruta conductor -> donante
      const startLat = this.currentDriverLat ?? this.pickupLat;
      const startLng = this.currentDriverLng ?? this.pickupLng;

      startPoint = L.latLng(startLat, startLng);
      endPoint = L.latLng(this.pickupLat, this.pickupLng);
    }else if (this._status === 'IN_TRANSIT'){
      //Ruta donante -> Ong
      const startLat = this.currentDriverLat ?? this.pickupLat;
      const startLng = this.currentDriverLng ?? this.pickupLng;

      startPoint = L.latLng(startLat, startLng);
      endPoint = L.latLng(this.dropOffLat, this.dropOffLng);
    } else {
      this.routingControl.setWaypoints([]);

      if(this.driverMarker && this.map){
        this.map.removeLayer(this.driverMarker);
        this.driverMarker = undefined;
      }
      return;
    }
    this.routingControl.setWaypoints([
      startPoint,
      endPoint
    ]);
  }

}
