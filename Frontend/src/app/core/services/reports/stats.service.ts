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
   * Get stats, optionally filtered by a range of dates
   * @returns Observable<T>
   */
  getStats(startDate?: string, endDate?: string) {
    let params = new HttpParams();
    if(startDate) params = params.set('startDate', startDate);
    if(endDate) params = params.set('endDate', endDate);
    return this.http.get<any>(this.apiUrl, { params });
  }

}
