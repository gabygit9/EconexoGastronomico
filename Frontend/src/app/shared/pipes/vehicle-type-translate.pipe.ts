import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'vehicleTypeTranslate'
})
export class VehicleTypeTranslatePipe implements PipeTransform {

  private readonly vehicleDictionary: Record<string, string> = {
    'CAR': 'Auto',
    'MOTORCYCLE': 'Moto',
    'BICYCLE': 'Bicicleta',
    'TRUCK': 'Camión',
    'KICK_SCOOTER': 'Monopatín',
    'PICKUP': 'Camioneta'
  };

  transform(value: string | undefined | null): string {
    if (!value) return 'No especificado';
    const key = value.toUpperCase().trim();
    return this.vehicleDictionary[key] || value;
  }

}
