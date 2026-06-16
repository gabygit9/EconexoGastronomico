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

  getAvailableTripsNearby(latitude: number, longitude:number): Observable<DonationResponse[]>{
    let params = new HttpParams()
      .set('latitude', latitude.toString())
      .set('longitude', longitude.toString());
    return this.http.get<DonationResponse[]>(`${this.apiUrl}/available-trips`, { params })
  }
}
