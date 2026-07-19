import {Component, computed, output, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-report-modal',
  imports: [
    FormsModule
  ],
  templateUrl: './report-modal.component.html',
  styleUrl: './report-modal.component.css'
})
export class ReportModalComponent {
  isOpen = signal(false);
  startDate = '';
  endDate = '';

  today = new Date().toISOString().split('T')[0];

  onConfirm = output<{ start: string, end: string }>();

  isFormValid(): boolean {
    if (!this.startDate || !this.endDate) return false;

    const start = new Date(this.startDate);
    const end = new Date(this.endDate);
    const todayDate = new Date(this.today);

    return start <= end && start <= todayDate && end <= todayDate;
  }

  confirm() {
    if (this.isFormValid()) {
      this.onConfirm.emit({ start: this.startDate, end: this.endDate });
    }
  }

  close() {
    this.isOpen.set(false);
    this.resetForm();
  }

  open() {
    this.isOpen.set(true);
  }

  private resetForm() {
    this.startDate = '';
    this.endDate = '';
  }
}
