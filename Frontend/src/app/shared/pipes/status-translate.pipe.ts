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
    'PENDING_PAYMENT': "Pendiente",
    'AVAILABLE': 'Disponible',
    'REQUESTED': 'Solicitado',
    'ASSIGNED': 'Asignado',
    'IN_TRANSIT': 'En tránsito',
    'DELIVERED_PENDING_NGO': 'En destino',
    'DELIVERED': 'Entregado',
    'REJECTED': 'Rechazado',
    'CANCELED': 'Cancelado',
    'EXPIRED': 'Expirado',
    'COMPLETED': 'Completado'
  }

  transform(value: Status | DonationStatus): string {
    if(value in this.statusLoginDictionary){
      return this.statusLoginDictionary[value as Status];
    }
    return this.statusDonationDictionary[value as DonationStatus] ?? value;
  }

}
