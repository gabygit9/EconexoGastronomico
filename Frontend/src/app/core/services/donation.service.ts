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

  /**
   * Request an available donation (Changes status to REQUESTED)
   * @param donationId - The ID of the donation to request
   */
  requestDonation(donationId: number){
    return this.http.patch<void>(`${this.apiUrl}/${donationId}/request`, {donationId});
  }

  /**
   * Get my donations (as a Donor or Ngo)
   * @returns An Observable of the user's donations
   */
  getMyDonations(): Observable<DonationResponse[]>{
    return this.http.get<DonationResponse[]>(`${this.apiUrl}/me`);
  }

  /**
   * Get a donation by ID
   * @param donationId - The ID of the donation to get
   * @returns An Observable of the donation
   */
  getDonationById(donationId: number): Observable<DonationResponse>{
    return this.http.get<DonationResponse>(`${this.apiUrl}/${donationId}`);
  }

  /**
   * Cancel a donation (as a Donor)
   * @param donationId
   */
  cancelDonationByDonor(donationId: number){
    return this.http.post<void>(`${this.apiUrl}/${donationId}/cancel`, {donationId});
  }

  /**
   * Reject a donation (as a Donor)
   * @param donationId
   */
  rejectDonationByDonor(donationId: number){
    return this.http.post<void>(`${this.apiUrl}/${donationId}/reject-driver`, {donationId});
  }
}
