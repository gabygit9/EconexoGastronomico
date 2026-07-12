package com.tfi.econexo.dto.donation.summary;

import io.swagger.v3.oas.annotations.media.Schema;

public record DonationItemSummaryDTO(

        @Schema(description = "Product name", example = "Lasagna")
        String productName,

        @Schema(description = "Quantity", example = "100")
        Integer quantity,

        @Schema(description = "Unit of measure", example = "Kg")
        String unitOfMeasure,

        @Schema(description = "Donation description", example = "Lasagna")
        String description,

        @Schema(description = "Allergen warning", example = "Contains peanuts")
        String allergenWarning


) {
}
