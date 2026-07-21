import {inject, Injectable} from '@angular/core';
import {NgoResponseDTO} from '../../shared/models/ngo.model';
import {Observable, tap} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {AuthService} from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class NgoService {

  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  private readonly apiUrl =`${environment.apiUrl}/v1/organizations`;


  getNgoProfile(): Observable<NgoResponseDTO>{
    return this.http.get<NgoResponseDTO>(`${this.apiUrl}/profile`).pipe(
      tap((profile) => {
        this.authService.setCurrentUser(profile);
      })
    );
  }

  getActiveNgos(){
    return this.http.get<NgoResponseDTO[]>(`${this.apiUrl}/active`);
  }
}
