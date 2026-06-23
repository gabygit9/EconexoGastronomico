package com.tfi.econexo.service.donation;

import com.tfi.econexo.dto.donation.DonationRequestDTO;
import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.dto.donation.DonationSummaryResponseDTO;
import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.enums.DonationStatus;
import org.locationtech.jts.geom.Point;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DonationService {

    DonationResponseDTO donate(DonationRequestDTO donationRequestDTO);
    List<DonationSummaryResponseDTO> getAvailableDonationsSummary();
    void requestDonation(Long donationId, String ngoEmail);
    List<Donation> findAvailableTripsNearby(
            @Param("driverLocation") Point driverLocation,
            @Param("driverId") Long driverId,
            @Param("status") DonationStatus status
    );
    Optional<Donation> findByIdDonation(Long id);
    Donation save(Donation donation);
    List<DonationResponseDTO> getMyDonations(String email);
    DonationResponseDTO getDonation(Long id);
}
