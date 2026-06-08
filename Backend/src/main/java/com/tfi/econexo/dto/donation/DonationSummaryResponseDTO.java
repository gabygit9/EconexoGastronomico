package com.tfi.econexo.dto.donation;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Donation summary response")
public record DonationSummaryResponseDTO(

        @Schema(description = "Donation ID", example = "1L")
        Long id,

        @Schema(description = "Business name", example = "El Hornito")
        String businessName,

        @Schema(description = "Expiration date", example = "2023-01-01T00:00:00")
        LocalDateTime expirationDate,

        @Schema(description = "Requires refrigeration", example = "true")
        boolean requiresRefrigeration,

        @Schema(description = "Donation items", example = "[1L, 2L]")
        List<DonationItemSummaryDTO> items
) {
}
