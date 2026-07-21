import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {UpdateStatusRequest, UserAdminResponse} from '../../shared/models/admin.model';
import {Observable} from 'rxjs';
import {Status} from '../../shared/models/login.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl =`${environment.apiUrl}/v1/admin/users`;


  /**
   * Get all users registered
   * @returns An Observable of users
   */
  getAllUsers(): Observable<UserAdminResponse[]> {
    return this.http.get<UserAdminResponse[]>(this.apiUrl);
  }

  /**
   * Update the status of register of a user
   * @param userId - The ID of the user
   * @param status - The new status of the user
   * @returns An Observable of the update response
   */
  updateUserStatus(userId: number, status: Status): Observable<void>{
    const body : UpdateStatusRequest = { status: status };
    return this.http.patch<void>(`${this.apiUrl}/${userId}/status`, body);
  }
}
