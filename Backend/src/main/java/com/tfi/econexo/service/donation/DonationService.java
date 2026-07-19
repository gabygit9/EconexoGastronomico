package com.tfi.econexo.service.donation;

import com.tfi.econexo.dto.donation.DonationRequestDTO;
import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.dto.donation.RejectionRequestDTO;
import com.tfi.econexo.dto.donation.summary.DonationSummaryResponseDTO;
import com.tfi.econexo.dto.reception.DonationItemReceptionDTO;
import com.tfi.econexo.dto.reception.ReceivedDonationDTO;
import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.enums.DonationStatus;
import org.locationtech.jts.geom.Point;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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
    void cancelTrip(Long donationId, String driverEmail);
    void rejectDonationByDriver(Long donationId, String driverEmail);
    void cancelDonationByDonor(Long donationId, String donorEmail);
    void rejectDriverByDonor(Long donationId, String donorEmail);
    void cancelDonationByNgo(Long donationId, String ngoEmail);
    void receiveDonation(Long donationId, ReceivedDonationDTO dto, String email);
    List<DonationItemReceptionDTO> getDonationItems(Long id);
    byte[] getCertificateBytes(Long id);
    byte[] getSummaryReport(Long donorId, LocalDate start, LocalDate end);
    void rejectDonationWithDetails(Long donationId, RejectionRequestDTO dto, String email);
}
