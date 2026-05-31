package com.tfi.econexo.dto.auth.logistics;

import com.tfi.econexo.model.logistics.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Vehicle response data")
public record VehicleResponseDTO(

        @Schema(description = "Database unique identifier of the vehicle", example = "1")
        Long id,

        @Schema(description = "Type of vehicle", example = "TRUCK")
        VehicleType vehicleType,

        @Schema(description = "Indicates if the vehicle has refrigeration", example = "true")
        Boolean hasRefrigeration,

        @Schema(description = "Capacity of the vehicle in kilograms", example = "10000")
        int capacityKg,

        @Schema(description = "Number plate of the vehicle", example = "ABC123")
        String numberPlate,

        @Schema(description = "Date of expiration of the vehicle's drivers license", example = "31-12-2025")
        LocalDate driversLicenseExpiration
) {
}
