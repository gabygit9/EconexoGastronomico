package com.tfi.econexo.dto.donation.item;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Donation item response")
public record DonationItemResponseDTO(

        @Schema(description = "Donation item ID", example = "1L")
        Long id,

        @Schema(description = "Product name", example = "Product name")
        String productName,

        @Schema(description = "Product category", example = "Product category")
        String category,

        @Schema(description = "Product type", example = "Product type")
        String productType,

        @Schema(description = "Quantity", example = "10.0")
        Double quantity,

        @Schema(description = "Unit of measure", example = "Unit of measure")
        String unitOfMeasure,

        @Schema(description = "Batch number", example = "Batch number")
        String batchNumber,

        @Schema(description = "Production date", example = "2023-01-01T00:00:00")
        LocalDateTime productionDate,

        @Schema(description = "Expiration date", example = "2023-01-01T00:00:00")
        LocalDateTime expirationDate,

        @Schema(description = "Delivery temperature", example = "10")
        String deliveryTemperature,

        @Schema(description = "Allergens warning", example = "This product contains peanuts")
        String allergenWarning,

        @Schema(description = "Observations", example = "This product is organic")
        String observations,

        @Schema(description = "Description", example = "Lasagna")
        String description
) {
}
