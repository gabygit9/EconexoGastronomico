package com.tfi.econexo.dto.donation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Donation request")
public record DonationRequestDTO(

        @Schema(description = "Pickup start time", example = "2023-01-01T00:00:00")
        @NotNull LocalDateTime pickupStartTime,

        @Schema(description = "Pickup end time", example = "2023-01-01T00:00:00")
        @NotNull LocalDateTime pickupEndTime,

        @Schema(description = "Donation items", example = "[Bakery, Meat, Dairy]")
        @NotEmpty List<DonationItemRequestDTO> items
        ) {
}
