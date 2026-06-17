package com.tfi.econexo.dto.logistics;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to accept a donation trip")
public record AcceptTripRequestDTO(

        @Schema(description = "ID of the vehicle to be used for the donation trip", example = "1L")
        @NotNull Long vehicleId
) {
}
