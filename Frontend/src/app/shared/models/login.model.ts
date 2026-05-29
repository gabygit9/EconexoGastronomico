export interface AuthLoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  email: string;
  message: string;
  jwt: string;
  status: boolean;
}

export type Status = 'PENDING' | 'APPROVED' | 'REJECTED' | 'SUSPENDED' | 'CANCELED';
