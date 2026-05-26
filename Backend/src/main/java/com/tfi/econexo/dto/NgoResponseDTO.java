package com.tfi.econexo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record NgoResponseDTO(
        @Schema(description = "Unique identifier of the organization", example = "1")
        Long id,

        @Schema(description = "User email", example = "user@example.com")
        String email,

        @Schema(description = "Organization name", example = "Fundación EcoNexo")
        String ngoName,

        @Schema(description = "Legal personality number", example = "123456789")
        String legalPersonalityNumber,

        @Schema(description = "Tax ID number", example = "123456789")
        String taxId,

        @Schema(description = "Legal representative name", example = "Juan Perez")
        String responsibleName,
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
        Long neighborhoodId
) {
}
