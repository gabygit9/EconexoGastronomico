package com.tfi.econexo.dto.donation;

import java.time.LocalDateTime;

public record DonationItemResponseDTO(
        Long id,
        String productName,
        Double quantity,
        String unitOfMeasure,
        String batchNumber,
        LocalDateTime productionDate,
        LocalDateTime expirationDate,
        String deliveryTemperature,
        String allergenWarning,
        String observations
) {
}
