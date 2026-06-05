import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment.development';
import {DonationRequest, DonationResponse} from '../../shared/models/donation.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DonationService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl =`${environment.apiUrl}/v1/donations`;

  /**
   * Donate a new donation
   * @param request - The donation request
   * @returns An Observable of the donation response
   */
  donate(request: DonationRequest): Observable<DonationResponse> {
    return this.http.post<DonationResponse>(`${this.apiUrl}/donate`, request);
  }
}
