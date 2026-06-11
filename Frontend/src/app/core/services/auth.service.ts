import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpRequest} from '@angular/common/http';
import {environment} from '../../../environments/environment.development';
import {
  DonorRegistrationRequest,
  DonorResponse,
  DonorTypeLookup,
  NeighborhoodLookup
} from '../../shared/models/donor.model';
import {BehaviorSubject, catchError, Observable, tap, throwError} from 'rxjs';
import {AuthLoginRequest, AuthResponse} from '../../shared/models/login.model';
import {NgoRegistrationDTO, NgoResponseDTO, NgoTypeLookup} from '../../shared/models/ngo.model';
import {DriverRegistrationDTO, DriverResponse} from '../../shared/models/driver.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl =`${environment.apiUrl}/v1/auth`;
  private readonly neighborhoodsUrl =`${environment.apiUrl}/v1/neighborhoods/public`;
  private readonly donorsUrl =`${environment.apiUrl}/v1/donors/public/donor-types`;
  private readonly ngosUrl = `${environment.apiUrl}/v1/organizations/public/ngo-types`;

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  private currentUserSubject = new BehaviorSubject<DonorResponse | NgoResponseDTO | DriverResponse | UserAdminResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  /**
   * Checks if there is a token in localStorage
   * @returns {boolean} - True if there is a token, false otherwise
   */
  private hasToken(): boolean{
    return !!localStorage.getItem('econexo_token');
  }

  /**
   * Sets the current user
   * @param user - The user to set
   */
  setCurrentUser(user: DonorResponse | NgoResponseDTO | DriverResponse | null){
    this.currentUserSubject.next(user);
  }

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
   * Login a user and store the token in localStorage
   * @param credentials - The user credentials
   * @returns An Observable of the login response
   */
  login(credentials: AuthLoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response) => {
        localStorage.setItem('econexo_token', response.jwt);
        this.isAuthenticatedSubject.next(true);
      })
    );
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

  /**
   * Logout the current user
   * @returns An Observable of the logout response
   */
  logout(): Observable<any> {
    return this.http.post<void>(`${this.apiUrl}/logout`, {}).pipe(
      tap(() => {
        this.clearLocalSession();
      }),
      catchError((error) => {
        this.clearLocalSession();
        return throwError(() => error);
      })
    );
  }

  /**
   * Clears the local session by removing the token and updating the authentication state
   */
  private clearLocalSession(){
    localStorage.removeItem('econexo_token');
    this.isAuthenticatedSubject.next(false);
  }
}
