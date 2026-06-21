import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../../environments/environment.development';
import {Observable} from 'rxjs';
import {DonationResponse} from '../../shared/models/donation.model';

@Injectable({
  providedIn: 'root'
})
export class LogisticsService {

  private readonly http = inject((HttpClient));
  private readonly apiUrl = `${environment.apiUrl}/v1/logistics`;

  /**
   * Gets available trips near a specific location
   * @param latitude
   * @param longitude
   */
  getAvailableTripsNearby(latitude: number, longitude:number): Observable<DonationResponse[]>{
    let params = new HttpParams()
      .set('latitude', latitude.toString())
      .set('longitude', longitude.toString());
    return this.http.get<DonationResponse[]>(`${this.apiUrl}/available-trips`, { params })
  }

  /**
   * Accepts a trip
   * @param donationId
   * @param vehicleId
   */
  acceptTrip(donationId: number, vehicleId: number): Observable<void> {
    const payload = { vehicleId };
    return this.http.post<void>(`${this.apiUrl}/trips/${donationId}/accept`, payload);
  }

  /**
   * Gets a specific trip by ID
   * @param id
   */
  getTripById(id: number): Observable<DonationResponse>{
    return this.http.get<DonationResponse>(`${this.apiUrl}/trips/${id}`);
  }
}
