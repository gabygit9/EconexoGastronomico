import {Status} from './login.model';

export interface UserAdminResponse {
  userId: number,
  name: string,
  email: string,
  userType: string,
  status: Status,
  createdDate: string,
  taxId: string,
  certificateUrl?: string,
  registrationNumber?: string
}

export interface UpdateStatusRequest {
  status: Status
}
