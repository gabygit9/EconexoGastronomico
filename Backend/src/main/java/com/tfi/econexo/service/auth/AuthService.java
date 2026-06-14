package com.tfi.econexo.service.auth;

import com.tfi.econexo.dto.auth.logistics.DriverRegistrationDTO;
import com.tfi.econexo.dto.auth.logistics.DriverResponseDTO;
import com.tfi.econexo.dto.auth.ngo.NgoRegistrationDTO;
import com.tfi.econexo.dto.auth.ngo.NgoResponseDTO;
import com.tfi.econexo.dto.auth.donor.DonorRegistrationDTO;
import com.tfi.econexo.dto.auth.donor.DonorResponseDTO;

public interface AuthService {

    DonorResponseDTO registerDonor(DonorRegistrationDTO donorRegistrationDTO);
    DriverResponseDTO registerDriver(DriverRegistrationDTO driverRegistrationDTO);
    NgoResponseDTO registerNgo(NgoRegistrationDTO ngoRegistrationDTO);
    void requestPasswordReset(String email);
    void confirmPasswordReset(String token, String newPassword);
}
