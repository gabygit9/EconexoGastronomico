import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';

export const timeWindowValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const start = control.get('pickupStartTime')?.value;
  const end = control.get('pickupEndTime')?.value;

  if (!start || !end) {
    return null;
  }

  // Convierte "HH:mm" a minutos totales del día
  const [startHours, startMinutes] = start.split(':').map(Number);
  const [endHours, endMinutes] = end.split(':').map(Number);

  const startTotalMinutes = startHours * 60 + startMinutes;
  const endTotalMinutes = endHours * 60 + endMinutes;

  // 'Hasta' no puede ser anterior o igual al 'Desde'
  if (endTotalMinutes <= startTotalMinutes) {
    return { timeOrderInvalid: true };
  }

  // Mínimo 60 minutos de diferencia
  if (endTotalMinutes - startTotalMinutes < 60) {
    return { minWindowInvalid: true };
  }

  return null;
};
