package com.tfi.econexo.service.donation;

import com.tfi.econexo.dto.donation.DonationRequestDTO;
import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.dto.donation.DonationSummaryResponseDTO;

import java.util.List;

public interface DonationService {

    DonationResponseDTO donate(DonationRequestDTO donationRequestDTO);
    List<DonationSummaryResponseDTO> getAvailableDonationsSummary();
    void requestDonation(Long donationId, String ngoEmail);
}
