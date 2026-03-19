package com.tfi.econexo.services.auth;

import com.tfi.econexo.dtos.auth.DonorRegistrationDTO;
import com.tfi.econexo.dtos.auth.DonorResponseDTO;

public interface AuthService {

    DonorResponseDTO registerDonor(DonorRegistrationDTO donorRegistrationDTO);
}
