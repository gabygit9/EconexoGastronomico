export interface DonationItemRequest {
  productId: number;
  quantity: number;
  batchNumber: string,
  productionDate: string,
  expirationDate: string,
  deliveryTemperature: string,
  allergenWarning: string,
  observations: string
}

export interface DonationItemResponse {
  id: number,
  productName: string,
  category: string,
  productType: string,
  quantity: number,
  unitOfMeasure: string,
  batchNumber: string,
  productionDate: string,
  expirationDate: string,
  deliveryTemperature: string,
  allergenWarning: string,
  observations: string
}

export interface DonationResponse {
  id: number,
  status: DonationStatus,
  pickupStartTime: string,
  pickupEndTime: string,
  createdAt: string,
  businessName: string,
  items: DonationItemResponse[]
}

export interface DonationRequest {
  pickupStartTime: string,
  pickupEndTime: string,
  items: DonationItemRequest[]
}

export type DonationStatus = 'AVAILABLE'| 'ASSIGNED' | 'IN_TRANSIT' | 'REJECTED' | 'DELIVERED' | 'CANCELED'
