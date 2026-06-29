import { Pipe, PipeTransform } from '@angular/core';
import {Status} from '../models/login.model';
import {DonationStatus} from '../models/donation.model';

@Pipe({
  name: 'statusTranslate'
})
export class StatusTranslatePipe implements PipeTransform {

  private readonly statusLoginDictionary: Record<Status, string> = {
    'PENDING': 'Pendiente',
    'APPROVED': 'Aprobado',
    'REJECTED': 'Rechazado',
    'SUSPENDED': 'Suspendido',
    'CANCELED': 'Cancelado'
  }

  private readonly statusDonationDictionary: Record<DonationStatus, string> = {
    'AVAILABLE': 'Disponible',
    'REQUESTED': 'Solicitado',
    'ASSIGNED': 'Asignado',
    'IN_TRANSIT': 'En tránsito',
    'DELIVERED_PENDING_NGO': 'Entregado (pendiente de ONG)',
    'DELIVERED': 'Entregado',
    'REJECTED': 'Rechazado',
    'CANCELED': 'Cancelado',
    'EXPIRED': 'Expirado'
  }

  transform(value: Status | DonationStatus): string {
    return value.valueOf().includes('APPROVED') ?
      this.statusLoginDictionary[value as Status] : this.statusDonationDictionary[value as DonationStatus];
  }

}
