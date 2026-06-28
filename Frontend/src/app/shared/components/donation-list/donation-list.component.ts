import {Component, computed, input, output, signal} from '@angular/core';
import {DatePipe, NgClass} from '@angular/common';
import {DonationResponse} from '../../models/donation.model';
import {StatusTranslatePipe} from '../../pipes/status-translate.pipe';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-donation-list',
  imports: [
    DatePipe, StatusTranslatePipe, RouterLink
  ],
  templateUrl: './donation-list.component.html',
  styleUrl: './donation-list.component.css'
})
export class DonationListComponent {

  donations = input.required<DonationResponse[]>();
  viewRole = input<'DONOR' | 'NGO'>();
  actionRequested = output<{donationId: number, type: 'CANCEL' | 'REJECT'}>();
  rowClick= output<number>();

  //Filtros
  currentFilter = signal<'ALL' | 'ACTIVE' | 'HISTORY'>('ACTIVE');

  filteredDonations = computed(() => {
    const all = this.donations();
    const filter = this.currentFilter();
    if(filter === 'ALL') return all;
    if(filter === 'ACTIVE') {
      return all.filter(d => ['AVAILABLE', 'REQUESTED', 'ASSIGNED', 'IN_TRANSIT'].includes(d.status));
    }
    if(filter === 'HISTORY') {
      return all.filter(d => ['DELIVERED', 'CANCELED', 'REJECTED', 'EXPIRED'].includes(d.status));
    }
    return all;
  })

  //Paginación
  currentPage = signal<number>(1);
  pageSize = signal<number>(5);

  paginateDonations = computed(() => {
    const start = (this.currentPage() - 1) * this.pageSize();
    const end = start + this.pageSize();
    return this.filteredDonations().slice(start, end);
  })

  totalPages = computed(() => {
    return Math.ceil(this.filteredDonations().length / this.pageSize()) || 1;
  })

  startItem = computed(() => (this.currentPage() - 1) * this.pageSize() + 1);
  endItem = computed(() => Math.min(this.currentPage() * this.pageSize(), this.filteredDonations().length));

  openAction(donation: DonationResponse, type: 'CANCEL' | 'REJECT') {
    this.actionRequested.emit({donationId: donation.id, type});
  }

  setFilter(filter: 'ALL' | 'ACTIVE' | 'HISTORY'){
    this.currentFilter.set(filter);
    this.currentPage.set(1);
  }

  nextPage(){
    if(this.currentPage() < this.totalPages()){
      this.currentPage.update(p => p + 1);
    }
  }

  prevPage(){
    if(this.currentPage() > 1){
      this.currentPage.update(p => p - 1);
    }
  }

  getStatusClass(status: string) {
    const statusMap: Record<string, string> = {
      'AVAILABLE': 'bg-blue-50 text-blue-700 border-blue-200',
      'REQUESTED': 'bg-purple-50 text-purple-700 border-purple-200',
      'ASSIGNED': 'bg-yellow-50 text-yellow-700 border-yellow-200',
      'IN_TRANSIT': 'bg-orange-50 text-orange-700 border-orange-200',
      'DELIVERED': 'bg-emerald-50 text-emerald-700 border-emerald-200',
      'CANCELED': 'bg-red-50 text-red-700 border-red-200',
      'EXPIRED': 'bg-gray-50 text-gray-600 border-gray-300'
    };
    return statusMap[status] || 'bg-gray-50 text-gray-700 border-gray-200';
  }

}
