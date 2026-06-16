package com.tfi.econexo.service.logistics;

import com.tfi.econexo.dto.donation.DonationResponseDTO;

import java.util.List;

public interface LogisticsService {
    List<DonationResponseDTO> getAvailableTripsNearby(String driverEmail, Double latitude, Double longitude);
}
