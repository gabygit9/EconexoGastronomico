package com.tfi.econexo.services.auth;

import com.tfi.econexo.dtos.auth.donor.DonorRegistrationDTO;
import com.tfi.econexo.dtos.auth.donor.DonorResponseDTO;
import com.tfi.econexo.dtos.auth.driver.DriverRegistrationDTO;
import com.tfi.econexo.dtos.auth.driver.DriverResponseDTO;
import com.tfi.econexo.dtos.auth.organization.OrganizationRegistrationDTO;
import com.tfi.econexo.dtos.auth.organization.OrganizationResponseDTO;

public interface AuthService {

    DonorResponseDTO registerDonor(DonorRegistrationDTO donorRegistrationDTO);
    DriverResponseDTO registerDriver(DriverRegistrationDTO driverRegistrationDTO);
    OrganizationResponseDTO registerOrganization(OrganizationRegistrationDTO organizationRegistrationDTO);
}
