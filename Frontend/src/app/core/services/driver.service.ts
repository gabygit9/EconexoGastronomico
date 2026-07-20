import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AuthService} from './auth.service';
import {environment} from '../../../environments/environment';
import {Observable, tap} from 'rxjs';
import {DriverResponse} from '../../shared/models/driver.model';

@Injectable({
  providedIn: 'root'
})
export class DriverService {

  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  private readonly apiUrl =`${environment.apiUrl}/v1/drivers`;



  getDriverProfile(): Observable<DriverResponse>{
    return this.http.get<DriverResponse>(`${this.apiUrl}/profile`).pipe(
      tap((profile) => {
        this.authService.setCurrentUser(profile);
      })
    );
  }

}
