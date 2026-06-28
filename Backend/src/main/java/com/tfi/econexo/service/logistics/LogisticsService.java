package com.tfi.econexo.service.logistics;

import com.tfi.econexo.dto.donation.DonationResponseDTO;

import java.util.List;

public interface LogisticsService {
    List<DonationResponseDTO> getAvailableTripsNearby(String driverEmail, Double latitude, Double longitude);
    void acceptTrip(Long donationId, String driverEmail, Long vehicleId);
    DonationResponseDTO getTripDetailsById(Long id);
    void updateTripStatus(Long tripId, String newStatus, String driverEmail);
}
