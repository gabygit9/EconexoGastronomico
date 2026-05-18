package com.tfi.econexo.service.auth;

import com.tfi.econexo.dto.auth.donor.DonorRegistrationDTO;
import com.tfi.econexo.dto.auth.donor.DonorResponseDTO;
import com.tfi.econexo.dto.auth.driver.DriverRegistrationDTO;
import com.tfi.econexo.dto.auth.driver.DriverResponseDTO;
import com.tfi.econexo.dto.auth.login.LoginRequestDTO;
import com.tfi.econexo.dto.auth.login.LoginResponseDTO;
import com.tfi.econexo.dto.auth.organization.OrganizationRegistrationDTO;
import com.tfi.econexo.dto.auth.organization.OrganizationResponseDTO;

public interface AuthService {

    DonorResponseDTO registerDonor(DonorRegistrationDTO donorRegistrationDTO);
    DriverResponseDTO registerDriver(DriverRegistrationDTO driverRegistrationDTO);
    OrganizationResponseDTO registerOrganization(OrganizationRegistrationDTO organizationRegistrationDTO);
    LoginResponseDTO login(LoginRequestDTO request);
}
