import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'ngoTypeTranslate'
})
export class NgoTypeTranslatePipe implements PipeTransform {

  transform(value: string): string {
    const translations: Record<string, string> = {
      'Soup kitchen': 'Comedor Social',
      'Community snack bar': 'Merendero',
      'Food bank': 'Banco de Alimentos',
      'Shelter': 'Refugio',
      'Community center': 'Centro Comunitario',
      'Elderly home': 'Hogar Adultos Mayores',
      'Children home': 'Hogar Niños',
      'Other': 'Otro'
    };
    return translations[value] || value;
  }

}
