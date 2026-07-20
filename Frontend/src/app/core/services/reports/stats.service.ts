import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class StatsService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl =`${environment.apiUrl}/v1/reports/stats`;

  /**
   * Get stats
   * @returns Observable<T>
   */
  getStats() {
    return this.http.get<any>(this.apiUrl);
  }

}
