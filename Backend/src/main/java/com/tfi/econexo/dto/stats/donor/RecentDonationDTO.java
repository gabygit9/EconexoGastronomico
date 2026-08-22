package com.tfi.econexo.dto.stats.donor;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO that represents a recent donation")
public record RecentDonationDTO(

        @Schema(description = "Name of the donor")
        String donorName,

        @Schema(description = "Date of the donation")
        LocalDateTime date,

        @Schema(description = "Quantity of the donation")
        Double quantity
) {
}
