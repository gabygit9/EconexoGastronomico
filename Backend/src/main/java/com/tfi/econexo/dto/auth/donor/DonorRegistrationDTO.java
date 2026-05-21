package com.tfi.econexo.dto.auth.donor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Data transfer object representing the payload required to register a new gastronomic donor commerce")
public record DonorRegistrationDTO(
        @Schema(description = "Authentication email address, must be unique within the platform", example = "contacto@elhornito.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Email String email,

        @Schema(description = "Secure password for account authentication, minimum length of 8 characters", example = "PasswordSegura123!", minLength = 8, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(min = 8) String password,

        @Schema(description = "Commercial name or brand name displayed to users in the application interface", example = "El Hornito Santiagueño", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String tradeName,

        @Schema(description = "Official registered legal or corporate name of the business entity", example = "Hornito Alimentos SRL", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String legalName,

        @Schema(description = "Unique national tax identification number (CUIT)", example = "30712345678", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String taxId,

        @Schema(description = "Contact phone number including area code, without spaces or hyphens", example = "351155123456", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String phoneNumber,

        @Schema(description = "Street name of the physical location of the commerce", example = "Av. Hipólito Yrigoyen", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String street,

        @Schema(description = "Street height number of the physical location", example = "450", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String streetNumber,

        @Schema(description = "Floor number inside the building structure, if applicable", example = "2", nullable = true)
        String floor,

        @Schema(description = "Apartment or office indicator letter/number, if applicable", example = "B", nullable = true)
        String apartment,

        @Schema(description = "Categorization of the gastronomic business type", example = "RESTAURANT", allowableValues = {"STORE", "SUPERMARKET", "RESTAURANT", "HOTEL", "BAR", "EVENT_HALL", "PRIVATE_CITIZEN", "BAKERY"}, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String donorType,

        @Schema(description = "Geographical latitude coordinate using WGS 84 GPS standard", example = "-31.4233", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Double latitude,

        @Schema(description = "Geographical longitude coordinate using WGS 84 GPS standard", example = "-64.1865", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Double longitude,

        @Schema(description = "Database unique identifier of the predefined neighborhood", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Long neighborhoodId
)
{}
