package com.tfi.econexo.dto.donation;

import com.tfi.econexo.dto.donation.item.DonationItemResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Donation response")
@Builder
public record DonationResponseDTO(

        @Schema(description = "Donation ID", example = "1L")
        Long id,

        @Schema(description = "Donation status", example = "PENDING")
        String status,

        @Schema(description = "Pickup start time", example = "2023-01-01T00:00:00")
        LocalDateTime pickupStartTime,

        @Schema(description = "Pickup end time", example = "2023-01-01T00:00:00")
        LocalDateTime pickupEndTime,

        @Schema(description = "Created at", example = "2023-01-01T00:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Business name", example = "Business Name")
        String businessName,

        @Schema(description = "Donor latitude", example = "40.7128")
        Double pickupLat,

        @Schema(description = "Donor longitude", example = "-40.7128")
        Double pickupLng,

        @Schema(description = "Ngo latitude", example = "40.7128")
        Double dropOffLat,

        @Schema(description = "Ngo longitude", example = "40.7128")
        Double dropOffLng,

        @Schema(description = "Donation items", example = "[...]")
        List<DonationItemResponseDTO> items
) {
}
