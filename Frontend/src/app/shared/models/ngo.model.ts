export interface NgoRegistrationDTO {
  ngoName: string,
  taxId: string,
  legalPersonalityNumber: string,
  responsibleName: string,
  street: string,
  streetNumber: string,
  floor: string,
  apartment: string,
  phoneNumber: string,
  neighborhoodId: number,
  latitude: number,
  longitude: number,
  email: string,
  password: string,
  ngoType: string
}

export interface NgoResponseDTO {
  id: number,
  email: string,
  ngoName: string,
  legalPersonalityNumber: string,
  taxId: string,
  responsibleName: string,
  phoneNumber: string,
  street: string,
  streetNumber: string,
  floor: string,
  apartment: string,
  neighborhoodId: number
}

export interface NgoTypeLookup {
  value: number;
  label: string;
}
