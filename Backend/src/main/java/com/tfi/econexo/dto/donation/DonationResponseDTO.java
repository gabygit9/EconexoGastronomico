package com.tfi.econexo.dto.donation;

import java.time.LocalDateTime;
import java.util.List;

public record DonationResponseDTO(
        Long id,
        String status,
        LocalDateTime pickupStartTime,
        LocalDateTime pickupEndTime,
        LocalDateTime createdAt,
        String businessName,
        List<DonationItemResponseDTO> items
) {
}
