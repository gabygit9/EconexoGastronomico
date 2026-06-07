import {FormGroup} from '@angular/forms';

export abstract class BaseFormComponent {

  abstract get form(): FormGroup;

  isInvalidField(field: string){
    const control = this.form.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  getErrorMessage(field: string) {
    const control = this.form.get(field);
    if (control && control.errors && (control.dirty || control.touched)) {
      if (control.errors['required']) return 'Este campo es obligatorio.';
      if (control.errors['email']) return 'El formato del email no es válido.';
      if (control.errors['minlength']) return `Mínimo ${control.errors['minlength'].requiredLength} caracteres`;
      if (control.errors['maxlength']) return `Máximo ${control.errors['maxlength'].requiredLength} caracteres`;
      if(control.errors['pattern']) return 'Formato inválido (ingrese sólo números sin guiones ni espacio)';
    }
    return ''
  }

}
