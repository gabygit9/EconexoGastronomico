import {Component, input, output} from '@angular/core';

@Component({
  selector: 'app-generic-modal',
  imports: [],
  templateUrl: './generic-modal.component.html',
  styleUrl: './generic-modal.component.css'
})
export class GenericModalComponent {
  isOpen = input.required<boolean>();
  title = input<string>('¿Estás seguro?');
  message = input<string>('');
  confirmText = input<string>('Confirmar');
  cancelText = input<string>('Cancelar');

  confirmButtonClass = input<string>('bg-[#eb5c0c] hover:bg-[#d4530b]');

  confirm = output<void>();
  cancel = output<void>();
}
