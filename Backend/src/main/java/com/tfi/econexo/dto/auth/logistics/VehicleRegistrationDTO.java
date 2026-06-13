package com.tfi.econexo.dto.auth.logistics;

import com.tfi.econexo.model.logistics.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Vehicle registration data")
public record VehicleRegistrationDTO(

        @Schema(description = "Type of vehicle", example = "TRUCK")
        @NotNull VehicleType vehicleType,

        @Schema(description = "Indicates if the vehicle has refrigeration", example = "true")
        @NotNull Boolean hasRefrigeration,

        @Schema(description = "Capacity of the vehicle in kilograms", example = "10000")
        int capacityKg,

        @Schema(description = "Number plate of the vehicle", example = "ABC123")
        String numberPlate,

        @Schema(description = "URL link to the driver's license front side", example = "https://example.com/driver-license-front.jpg")
        String driversLicenseFrontUrl,

        @Schema(description = "URL link to the driver's license back side", example = "https://example.com/driver-license-front.jpg")
        String driversLicenseBackUrl,

        @Schema(description = "Date of expiration of the driver's license", example = "31-12-2025")
        LocalDate driversLicenseExpiration
) {
}
