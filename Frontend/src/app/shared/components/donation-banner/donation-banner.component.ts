import {Component, output} from '@angular/core';

@Component({
  selector: 'app-donation-banner',
  imports: [],
  templateUrl: './donation-banner.component.html',
  styleUrl: './donation-banner.component.css'
})
export class DonationBannerComponent {
  donate = output<void>();
}
