import { Injectable } from '@angular/core';
import {DonationStatus} from '../../shared/models/donation.model';

@Injectable({
  providedIn: 'root'
})
export class DonationStatusColorsService {

  private readonly hexColors: Record<DonationStatus, string> = {
    'PENDING_PAYMENT': '#f59e0b',
    'AVAILABLE': '#6366f1',
    'REQUESTED': '#3b82f6',
    'ASSIGNED': '#8b5cf6',
    'IN_TRANSIT': '#0ea5e9',
    'DELIVERED_PENDING_NGO': '#fbbf24',
    'DELIVERED': '#059669',
    'REJECTED': '#ef4444',
    'CANCELED': '#64748b',
    'EXPIRED': '#94a3b8',
    'COMPLETED': '#10b981'
  };

  getHexColor(status: string): string {
    const key = String(status).trim().toUpperCase() as DonationStatus;
    return this.hexColors[key] ?? '#94a3b8';
  }

}
