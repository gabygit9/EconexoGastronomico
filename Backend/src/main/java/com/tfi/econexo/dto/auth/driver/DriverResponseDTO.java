package com.tfi.econexo.dto.auth.driver;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Driver response data")
public record DriverResponseDTO(

        @Schema(description = "Database unique identifier of the driver", example = "1")
        Long id,

        @Schema(description = "First name of the driver", example = "John")
        String firstName,

        @Schema(description = "Last name of the driver", example = "Doe")
        String lastName,

        @Schema(description = "Authentication email address, must be unique within the platform", example = "contacto@elhornito.com")
        String email,

        @Schema(description = "Unique national tax identification number (CUIT)", example = "30712345678")
        String taxId,

        @Schema(description = "Contact phone number including area code, without spaces or hyphens", example = "351155123456")
        String phoneNumber,

        @Schema(description = "Date of birth of the driver", example = "31-12-2025")
        LocalDate birthDate,

        @Schema(description = "Registration status", example = "APPROVED")
        String status,

        @Schema(description = "Date of expiration of the driver's health booklet", example = "31-12-2025")
        LocalDate healthBookletExpiration,

        @Schema(description = "Street name of the physical location of the commerce", example = "Av. Hipólito Yrigoyen")
        String street,

        @Schema(description = "Street height number of the physical location", example = "450")
        String streetNumber,

        @Schema(description = "Floor number inside the building structure, if applicable", example = "2", nullable = true)
        String floor,

        @Schema(description = "Apartment or office indicator letter/number, if applicable", example = "B", nullable = true)
        String apartment,

        @Schema(description = "Name of the neighborhood", example = "Hipólito Yrigoyen")
        String neighborhoodName,

        @Schema(description = "List of vehicles registered by the driver")
        List<VehicleResponseDTO> vehicles
) {
}
