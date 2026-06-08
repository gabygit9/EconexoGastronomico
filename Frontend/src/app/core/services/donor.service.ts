import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AuthService} from './auth.service';
import {environment} from '../../../environments/environment.development';
import {Observable, tap} from 'rxjs';
import {DonorResponse} from '../../shared/models/donor.model';

@Injectable({
  providedIn: 'root'
})
export class DonorService {

  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  private readonly apiUrl =`${environment.apiUrl}/v1/donors`;


  getDonorProfile(): Observable<DonorResponse>{
    return this.http.get<DonorResponse>(`${this.apiUrl}/profile`).pipe(
      tap((profile) => {
        this.authService.setCurrentUser(profile);
      })
    );
  }
}
