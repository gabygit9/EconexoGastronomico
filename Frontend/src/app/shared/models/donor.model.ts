import {Status} from './login.model';

export interface DonorRegistrationRequest {
  email: string;
  password?:string;
  tradeName: string;
  legalName: string;
  taxId: string;
  phoneNumber: string;
  street: string;
  streetNumber: string;
  floor?: string | null;
  apartment?: string | null;
  donorType: string;
  latitude: number;
  longitude: number;
  neighborhoodId: number;
}

export interface DonorResponse {
  id: number;
  email: string;
  tradeName: string;
  legalName: string;
  taxId: string;
  phoneNumber: string;
  street: string;
  streetNumber: string;
  floor?: string | null;
  apartment?: string | null;
  neighborhoodId: number;
  status: Status
}

export interface NeighborhoodLookup {
  id: number;
  name: string;
}

export interface DonorTypeLookup {
  value: number;
  label: string;
}
