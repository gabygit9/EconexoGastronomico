import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment.development';
import {LandingStats} from '../../shared/models/stats.model';

@Injectable({
  providedIn: 'root'
})
export class LandingService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/v1/public/landing-stats`;

  /**
   * Get landing stats
   */
  getLandingStats() {
    return this.http.get<LandingStats>(this.apiUrl);
  }
}
