import {Status} from './login.model';

export interface DriverRegistrationDTO {
  firstName: string,
  lastName: string,
  taxId: string,
  birthDate: Date,
  email: string,
  password: string,
  foodHandlerCertificateUrl: string,
  foodHandlerCertificateExpiration: Date,
  vehicle: VehicleRegistrationDTO,
  phoneNumber: string,
  street: string,
  streetNumber: string,
  floor: string,
  apartment: string,
  latitude: number,
  longitude: number,
  neighborhoodId: number
}

export interface DriverResponse {
  id: number,
  firstName: string,
  lastName: string,
  email: string,
  taxId: string,
  phoneNumber: string,
  birthDate: Date,
  status: Status,
  foodHandlerCertificateExpiration: Date,
  street: string,
  streetNumber: string,
  floor: string,
  apartment: string,
  neighborhoodName: string
  vehicles: VehicleResponseDTO[],
}

export interface VehicleRegistrationDTO {
  vehicleType: VehicleType,
  hasRefrigeration: boolean,
  capacityKg: number,
  numberPlate: string,
  driversLicenseFrontUrl: string,
  driversLicenseBackUrl: string,
  driversLicenseExpiration: Date
}

export interface VehicleResponseDTO {
  id: number,
  vehicleType: VehicleType,
  hasRefrigeration: boolean,
  capacityKg: number,
  numberPlate: string,
  driversLicenseFrontUrl: string,
  driversLicenseBackUrl: string,
  driversLicenseExpiration: Date
}

export interface DriverSummaryDTO {
  firstName: string;
  lastName: string;
  numberPlate: string;
  vehicleType: string;
}

export type VehicleType = 'CAR' | 'TRUCK' | 'BICYCLE' | 'MOTORCYCLE' | 'KICK_SCOOTER' | 'PICKUP'
