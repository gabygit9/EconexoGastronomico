import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'donorTypeTranslatePipe'
})
export class DonorTypeTranslatePipe implements PipeTransform {

  transform(value: string): string {
    const translations: Record<string, string> = {
      'Restaurant': 'Restaurante',
      'Bakery': 'Panadería',
      'Supermarket': 'Supermercado',
      'Store': 'Almacén',
      'Event hall': 'Salón de eventos',
      'Private citizen': 'Particular'
    };
    return translations[value] || value;
  }

}
