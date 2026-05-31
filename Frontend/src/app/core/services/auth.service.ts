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

  private readonly apiUrl =`${environment.apiUrl}/v1/auth`;
  private readonly neighborhoodsUrl =`${environment.apiUrl}/v1/neighborhoods/public`;
  private readonly donorsUrl =`${environment.apiUrl}/v1/donors/public/donor-types`;
  private readonly ngosUrl = `${environment.apiUrl}/v1/organizations/public/ngo-types`;

  /**
   * Register a new donor
   * @param donorData - The donor data to register
   * @returns An Observable of the registered donor
   */
  registerDonor(donorData: DonorRegistrationRequest): Observable<DonorResponse> {
    return this.http.post<DonorResponse>(`${this.apiUrl}/register/donor`, donorData);
  }

  /**
   * Get all neighborhoods
   * @returns An Observable of neighborhoods
   */
  getNeighborhoods(): Observable<NeighborhoodLookup[]> {
    return this.http.get<NeighborhoodLookup[]>(this.neighborhoodsUrl);
  }

  /**
   * Get all donor types
   * @returns An Observable of donor types
   */
  getDonorTypes(): Observable<DonorTypeLookup[]> {
    return this.http.get<DonorTypeLookup[]>(this.donorsUrl);
  }

  /**
   * Get all NGO types
   * @returns An Observable of NGO types
   */
  getNgoTypes(): Observable<NgoTypeLookup[]> {
    return this.http.get<NgoTypeLookup[]>(this.ngosUrl);
  }

  /**
   * Login a user
   * @param credentials - The user credentials
   * @returns An Observable of the login response
   */
  login(credentials: AuthLoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials);
  }

  /**
   * Register a new NGO
   * @param ngoData - The NGO data to register
   * @returns An Observable of the registered NGO
   */
  registerNgo(ngoData:NgoRegistrationDTO): Observable<NgoResponseDTO>{
    return this.http.post<NgoResponseDTO>(`${this.apiUrl}/register/ngo`, ngoData);
  }

  /**
   * Register a new Driver
   * @param driverData - The driver data to register
   * @returns An Observable of the registered driver
   */
  registerDriver(driverData:DriverRegistrationDTO): Observable<DriverResponse>{
    return this.http.post<DriverResponse>(`${this.apiUrl}/register/driver`, driverData);
  }
  
  //TODO implementar en el backend
  getNgoProfile(): Observable<NgoResponseDTO>{
    return this.http.get<NgoResponseDTO>(`${this.apiUrl}/profile`);
  }
}
