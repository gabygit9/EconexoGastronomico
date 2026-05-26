package com.tfi.econexo.service.auth;

import com.tfi.econexo.dto.NgoRegistrationDTO;
import com.tfi.econexo.dto.NgoResponseDTO;
import com.tfi.econexo.dto.auth.donor.DonorRegistrationDTO;
import com.tfi.econexo.dto.auth.donor.DonorResponseDTO;
import com.tfi.econexo.dto.auth.driver.DriverRegistrationDTO;
import com.tfi.econexo.dto.auth.driver.DriverResponseDTO;
import com.tfi.econexo.dto.auth.login.AuthLoginRequestDTO;
import com.tfi.econexo.dto.auth.login.AuthResponseDTO;
import com.tfi.econexo.dto.auth.organization.OrganizationRegistrationDTO;
import com.tfi.econexo.dto.auth.organization.OrganizationResponseDTO;

public interface AuthService {

    DonorResponseDTO registerDonor(DonorRegistrationDTO donorRegistrationDTO);
    //DriverResponseDTO registerDriver(DriverRegistrationDTO driverRegistrationDTO);
    NgoResponseDTO registerNgo(NgoRegistrationDTO ngoRegistrationDTO);
    //AuthResponseDTO login(AuthLoginRequestDTO request);
}
