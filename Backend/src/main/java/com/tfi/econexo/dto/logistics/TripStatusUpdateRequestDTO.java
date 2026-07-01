package com.tfi.econexo.dto.logistics;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request to update the status of a donation trip")
public record TripStatusUpdateRequestDTO(

        @Schema(description = "Status to update", example = "IN_TRANSIT")
        @Pattern(regexp = "^(IN_TRANSIT|DELIVERED_PENDING_NGO|DELIVERED)$", message = "Transition status not allowed")
        @NotBlank String status
) {
}
