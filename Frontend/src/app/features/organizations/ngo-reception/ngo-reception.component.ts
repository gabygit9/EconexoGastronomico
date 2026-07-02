import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {DonationService} from '../../../core/services/donation.service';
import {DonationItemReception} from '../../../shared/models/donation.model';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-ngo-reception',
  imports: [
    FormsModule
  ],
  templateUrl: './ngo-reception.component.html',
  styleUrl: './ngo-reception.component.css'
})
export class NgoReceptionComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly donationService = inject(DonationService);
  private readonly toastr = inject(ToastrService);
  private readonly destroyRef = inject(DestroyRef);

  donationId = signal<number | null>(null);
  items = signal<DonationItemReception[]>([]);
  isLoading = signal<boolean>(true);
  comments = signal('');

  ngOnInit(){
    const id = this.route.snapshot.paramMap.get('id');
    if(id){
      this.donationId.set(+id);
      this.loadItems(+id);
    }
  }

  loadItems(id: number){
    this.donationService.getDonationItems(id).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (data) => {
        this.items.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.toastr.error('Error al cargar los items');
        this.goBack();
      }
    });
  }

  confirmReception(){
    if(!this.donationId()) return;

    this.donationService.receiveDonations(this.donationId()!, { comments: this.comments() }).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
        this.toastr.success('Donación recibida correctamente');
        this.goBack();
      },
      error: () => {
        this.toastr.error('Error al confirmar recepción');
      }
    })
  }

  goBack(){
    this.router.navigate(['/dashboard/ngo']);
  }

}
