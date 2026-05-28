import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LocationService {

  /**
   * Returns mock coordinates for a given neighborhood ID
   * @param neighborhoodId
   */
  getMockCoordinates(neighborhoodId: number): { latitude: number, longitude: number} | null {
    if(neighborhoodId === 1) return { latitude: -31.4233, longitude: -64.1865 };
    if(neighborhoodId === 2) return { latitude: -31.4125, longitude: -64.1678 };
    return null;
  }

  /**
   * Returns mock coordinates for a given address
   * @param street
   * @param streetNumber
   */
  async geocodeAddress(street: string, streetNumber: string): Promise<{ latitude: number, longitude: number } | null> {
    if(!street || !streetNumber) return null;
    console.log(`Ready to geocodify address : ${street} ${streetNumber}, Córdoba, Argentina`);

    // TODO: Implementar llamada real a Google Geocoding API
    // const coords = await this.googleService.getCoordinates(`${street} ${streetNumber}, Córdoba, Argentina`);
    // return { latitude: coords.lat, longitude: coords.lng };

    return null; // Retorno nulo temporal hasta el Sprint 3)
  }
}
