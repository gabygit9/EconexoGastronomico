import {Component, Input, output} from '@angular/core';

@Component({
  selector: 'app-donation-banner',
  imports: [],
  templateUrl: './donation-banner.component.html',
  styleUrl: './donation-banner.component.css'
})
export class DonationBannerComponent {
  @Input() title: string = 'Apoyá a nuestras ONGs';
  @Input() message: string = 'Tu donación económica es recibida directamente por las organizaciones que gestionan el rescate y la entrega de alimentos.';
  @Input() buttonText: string = 'Donar';

  donate = output<void>();
}
