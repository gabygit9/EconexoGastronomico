import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment.development';
import {
  Category,
  DonationRequest,
  DonationResponse,
  DonationSummaryResponse,
  Product,
  UnitOfMeasure
} from '../../shared/models/donation.model';
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

  /**
   * Get all categories
   * @returns An Observable of the categories
   */
  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/catalog/categories`);
  }

  /**
   * Get all products
   * @returns An Observable of the products
   */
  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/catalog/products`);
  }

  /**
   * Get all units of measure
   * @returns An Observable of the units of measure
   */
  getUnitOfMeasures(): Observable<UnitOfMeasure[]> {
    return this.http.get<UnitOfMeasure[]>(`${this.apiUrl}/catalog/units`);
  }

  /**
   * Get all available donations
   * @returns An Observable of the available donations
   */
  getAvailableDonations(): Observable<DonationSummaryResponse[]> {
    return this.http.get<DonationSummaryResponse[]>(`${this.apiUrl}/available`);
  }
}
