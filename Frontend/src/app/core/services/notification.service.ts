import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {NotificationDto} from '../../shared/models/donation.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private readonly http = inject((HttpClient));
  private readonly apiUrl = `${environment.apiUrl}/v1/notifications`;


  /**
   * Get the count of unread notifications
   * @returns Observable<number>
   */
  getUnreadCount() {
    return this.http.get<number>(`${this.apiUrl}/unread/count`);
  }

  /**
   * Get the list of notifications for the current user
   * @returns Observable<NotificationDto[]>
   */
  getMyNotifications() {
    return this.http.get<NotificationDto[]>(`${this.apiUrl}`);
  }

  /**
   * Mark all notifications as read
   * @returns Observable<void>
   */
  markAllAsRead(){
    return this.http.put<void>(`${this.apiUrl}/read`, {});
  }

  /**
   * Delete a specific notification
   * @param id - The ID of the notification to delete
   * @returns Observable<void>
   */
  deleteNotification(id: number) {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Delete all notifications for the current user
   * @returns Observable<void>
   */
  deleteAllNotifications() {
    return this.http.delete<void>(`${this.apiUrl}/all`);
  }
}
