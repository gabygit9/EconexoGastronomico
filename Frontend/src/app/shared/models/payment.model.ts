export interface PaymentRequest {
  donationId: number|null;
  ngoId?: number|null;
  amount: number;
  description: string;
}
