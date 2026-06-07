export interface DonationItemRequest {
  productId: number;
  quantity: number;
  batchNumber?: string,
  productionDate?: string,
  expirationDate: string,
  deliveryTemperature?: string,
  allergenWarning?: string,
  observations?: string,
  description?: string,
  unitOfMeasureId?: number
}

export interface DonationItemResponse {
  id: number,
  productName: string,
  category: string,
  productType: string,
  quantity: number,
  unitOfMeasure: string,
  batchNumber?: string,
  productionDate?: string,
  expirationDate: string,
  deliveryTemperature?: string,
  allergenWarning?: string,
  observations?: string,
  description?: string
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

export interface Category {
  id: number,
  description: string
}

export interface UnitOfMeasure {
  id: number,
  description: string
}

export interface Product {
  id: number,
  name: string,
  categoryId: number,
  requiresRefrigeration: boolean,
  isOriginalPackaging: boolean
}

export type DonationStatus = 'AVAILABLE'| 'ASSIGNED' | 'IN_TRANSIT' | 'REJECTED' | 'DELIVERED' | 'CANCELED'
