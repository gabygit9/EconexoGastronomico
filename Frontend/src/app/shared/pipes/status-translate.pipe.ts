import { Pipe, PipeTransform } from '@angular/core';
import {Status} from '../models/login.model';

@Pipe({
  name: 'statusTranslate'
})
export class StatusTranslatePipe implements PipeTransform {

  private readonly statusDictionary: Record<Status, string> = {
    'PENDING': 'Pendiente',
    'APPROVED': 'Aprobado',
    'REJECTED': 'Rechazado',
    'SUSPENDED': 'Suspendido',
    'CANCELED': 'Cancelado'
  }

  transform(value: Status): string {
    return this.statusDictionary[value] || value;
  }

}
