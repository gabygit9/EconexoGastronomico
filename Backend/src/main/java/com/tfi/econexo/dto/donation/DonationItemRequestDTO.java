package com.tfi.econexo.dto.donation;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DonationItemRequestDTO(
        @NotNull Long productId,
        @NotNull @Positive Double quantity,
        String batchNumber,
        LocalDateTime productionDate,
        @NotNull @Future LocalDate expirationDate,
        String deliveryTemperature,
        String allergenWarning,
        String observations
) {
}
