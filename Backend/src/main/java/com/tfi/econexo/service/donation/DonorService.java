package com.tfi.econexo.service.donation;

import com.tfi.econexo.dto.auth.donor.DonorResponseDTO;
import com.tfi.econexo.model.donation.donor.Donor;

import java.util.Optional;

public interface DonorService {
    Boolean findByTaxId(String taxId);
    Boolean existsEmail(String email);
    Donor save(Donor donor);
    Optional<Donor> findByUserEmail(String email);
    DonorResponseDTO getProfileByEmail(String email);
}
