import {DriverSummaryDTO} from './driver.model';

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
  ngoName: string,
  pickupAddress: string,
  dropOffAddress: string,
  pickupLat: number,
  pickupLng: number,
  dropOffLat: number,
  dropOffLng: number,
  items: DonationItemResponse[],
  driverInfo?: DriverSummaryDTO | null;
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

export interface DonationItemSummary {
  productName: string;
  quantity: number;
  unitOfMeasure: string;
  description?: string;
  allergenWarning?: string;
}

export interface DonationSummaryResponse {
  id: number;
  businessName: string;
  expirationDate: string;
  requiresRefrigeration: boolean;
  items: DonationItemSummary[];
}

export interface NotificationDto {
  id:number;
  message: string;
  isRead: boolean;
  createdAt: string;
}

export interface DeliveryEvidence {
  temperature: number;
  evidencePhotoUrl: string;
  driverSignatureUrl: string;
}

export interface DonationItemReception {
  itemId: number,
  productName: string,
  expectedQuantity: number,
  unitOfMeasure: string,
  description: string
}

export interface ReceivedItem {
  itemId: number,
  receivedQuantity: number
}

export interface ReceivedDonation {
  comments: string
}

export type DonationStatus = 'AVAILABLE'| 'REQUESTED' | 'ASSIGNED' | 'IN_TRANSIT' | 'REJECTED' | 'DELIVERED_PENDING_NGO' | 'DELIVERED' | 'CANCELED' | 'EXPIRED'
