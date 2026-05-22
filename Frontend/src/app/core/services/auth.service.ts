import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment.development';
import {DonorRegistrationRequest, DonorResponse} from '../../shared/models/donor.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl =`${environment.apiUrl}/v1/auth/register/donor`;

  /**
   * Register a new donor
   * @param donorData - The donor data to register
   * @returns An Observable of the registered donor
   */
  registerDonor(donorData: DonorRegistrationRequest): Observable<DonorResponse> {
    return this.http.post<DonorResponse>(this.apiUrl, donorData);
  }
}
