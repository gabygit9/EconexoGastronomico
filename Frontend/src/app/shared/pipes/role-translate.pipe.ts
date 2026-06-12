import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'roleTranslate'
})
export class RoleTranslatePipe implements PipeTransform {

  private readonly roleDictionary: Record<string, string> = {
    'DONOR': 'Donante',
    'DRIVER': 'Conductor',
    'NGO': 'ONG'
  }
  transform(value: string): string {
    return this.roleDictionary[value] || value;
  }

}
