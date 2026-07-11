import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment.development';
import {PaymentRequest} from '../../shared/models/payment.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl =`${environment.apiUrl}/v1/payments`;

  /**
   * Create a payment preference
   * @param dto - The payment request
   * @returns An Observable of the payment response
   */
  createPreference(dto: PaymentRequest): Observable<{ initPoint: string }> {
    return this.http.post<{ initPoint: string }>(`${this.apiUrl}/create-preference`, dto);
  }

  /**
   * Initiate a donation
   * @param dto - The payment request
   * @returns An Observable of the donation id
   */
  initiateDonation(dto: PaymentRequest){
    return this.http.post<number>(`${this.apiUrl}/money-donations`, dto);
  }

}
