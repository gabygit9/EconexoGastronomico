package com.tfi.econexo.dto.logistics;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Driver summary")
public record DriverSummaryDTO(

        @Schema(description = "Driver first name")
        String firstName,

        @Schema(description = "Driver last name")
        String lastName,

        @Schema(description = "Driver's number plate")
        String numberPlate,

        @Schema(description = "Driver's vehicle type")
        String vehicleType
) {
}
