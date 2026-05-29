package com.tfi.econexo.dto.auth.ngo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Data transfer object representing the profile request for a new ngo")
public record NgoRegistrationDTO(

        @Schema(description = "Organization name", example = "Fundación EcoNexo")
        @NotBlank String ngoName,

        @Schema(description = "Tax ID number", example = "123456789")
        @NotBlank String taxId,

        @Schema(description = "Legal personality number", example = "123456789")
        @NotBlank String legalPersonalityNumber,

        @Schema(description = "Legal representative name", example = "Juan Perez")
        @NotBlank String responsibleName,

        @Schema(description = "Physical street address name", example = "Av. Hipólito Yrigoyen")
        @NotBlank String street,

        @Schema(description = "Physical street address height number", example = "450")
        @NotBlank String streetNumber,

        @Schema(description = "Floor number inside the building structure, if applicable", example = "2", nullable = true)
        String floor,

        @Schema(description = "Apartment or office indicator letter/number, if applicable", example = "B", nullable = true)
        String apartment,

        @Schema(description = "Registered contact telephone line", example = "351155123456")
        @NotBlank String phoneNumber,

        @Schema(description = "Database unique identifier of the designated neighborhood entity", example = "1")
        @NotNull Long neighborhoodId,

        @Schema(description = "Latitude of the organization's location", example = "40.4165")
        @NotNull Double latitude,

        @Schema(description = "Longitude of the organization's location", example = "-3.7038")
        @NotNull Double longitude,

        @Schema(description = "User email", example = "user@example.com")
        @NotBlank @Email String email,

        @Schema(description = "User password", example = "password12345678")
        @NotBlank @Size(min = 8) String password,

        @Schema(description = "Organization type", example = "NGO")
        @NotBlank String ngoType
) {
}
