package com.tfi.econexo.dto.donation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Schema(description = "Donation item request")
public record DonationItemRequestDTO(

        @Schema(description = "Product ID", example = "1L")
        @NotNull Long productId,

        @Schema(description = "Quantity", example = "1.0")
        @NotNull @Positive Double quantity,

        @Schema(description = "Batch number", example = "123456789")
        String batchNumber,

        @Schema(description = "Production date", example = "2023-01-01T00:00:00")
        LocalDateTime productionDate,

        @Schema(description = "Expiration date", example = "2023-01-01")
        @NotNull @Future LocalDateTime expirationDate,

        @Schema(description = "Delivery temperature", example = "10")
        String deliveryTemperature,

        @Schema(description = "Allergen warning", example = "This product contains peanuts")
        String allergenWarning,

        @Schema(description = "Observations", example = "This product is organic")
        String observations
) {
}
