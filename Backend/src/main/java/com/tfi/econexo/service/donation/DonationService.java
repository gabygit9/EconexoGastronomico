package com.tfi.econexo.service.donation;

import com.tfi.econexo.dto.donation.DonationRequestDTO;
import com.tfi.econexo.dto.donation.DonationResponseDTO;

public interface DonationService {

    DonationResponseDTO donate(DonationRequestDTO donationRequestDTO);
}
