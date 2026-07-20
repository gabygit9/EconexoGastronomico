import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {firstValueFrom} from 'rxjs';
import {environment} from '../../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class LocationService {
  private readonly http = inject(HttpClient);
  private readonly apiKey = environment.googleMapsApiKey;

  /**
   * Returns coordinates for a given address
   * @param street
   * @param number
   */
  async geocodeAddress(street: string, number: string): Promise<{ latitude: number, longitude: number } | null> {
    if(!street || !number) return null;
    console.log(`Ready to geocodify address : ${street} ${number}, Córdoba, Argentina`);

    const address = `${street} ${number}, Córdoba, Argentina`;
    const url = `https://maps.googleapis.com/maps/api/geocode/json?address=${encodeURIComponent(address)}&key=${this.apiKey}`;

    try {
      const response: any = await firstValueFrom(this.http.get(url));
      if (response.status === 'OK' && response.results.length > 0) {
        const { lat, lng } = response.results[0].geometry.location;
        return { latitude: lat, longitude: lng };
      }
    } catch (error) {
      console.error('Error geocoding:', error);
    }
    return null;
  }

}
