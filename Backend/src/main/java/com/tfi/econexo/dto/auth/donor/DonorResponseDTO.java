package com.tfi.econexo.dto.auth.donor;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object representing the profile response returned after a successful donor registration")
public record DonorResponseDTO(
        @Schema(description = "Unique auto-generated database identifier for the specific donor profile", example = "1")
        Long id,

        @Schema(description = "Registered authentication login email associated with the profile", example = "contacto@elhornito.com")
        String email,

        @Schema(description = "Registered commercial brand name", example = "El Hornito Santiagueño")
        String tradeName,

        @Schema(description = "Registered corporate legal company name", example = "Hornito Alimentos SRL")
        String legalName,

        @Schema(description = "Registered unique national tax identification number (CUIT)", example = "30712345678")
        String taxId,

        @Schema(description = "Registered contact telephone line", example = "351155123456")
        String phoneNumber,

        @Schema(description = "Physical street address name", example = "Av. Hipólito Yrigoyen")
        String street,

        @Schema(description = "Physical street location height number", example = "450")
        String streetNumber,

        @Schema(description = "Floor level number within the property structure", example = "0", nullable = true)
        String floor,

        @Schema(description = "Apartment, office, or local subunit identifier string", example = "A", nullable = true)
        String apartment,

        @Schema(description = "Database unique identifier of the designated neighborhood entity", example = "1")
        Long neighborhoodId,

        @Schema(description = "Registration status", example = "PENDING")
        String status
) {
}
