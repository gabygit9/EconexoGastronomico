package com.tfi.econexo.dto.auth.logistics;

import com.tfi.econexo.utils.validation.ValidVehicleRequirements;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Driver registration data")
@ValidVehicleRequirements
public record DriverRegistrationDTO(

        @Schema(description = "First name of the driver", example = "John")
        @NotBlank String firstName,

        @Schema(description = "Last name of the driver", example = "Doe")
        @NotBlank String lastName,

        @Schema(description = "Unique national tax identification number (CUIT)", example = "30712345678")
        @NotBlank String taxId,

        @Schema(description = "Date of birth of the driver", example = "31-12-2025")
        @NotNull LocalDate birthDate,

        @Schema(description = "Authentication email address, must be unique within the platform", example = "contacto@elhornito.com")
        @NotBlank @Email String email,

        @Schema(description = "Secure password for account authentication, minimum length of 8 characters", example = "PasswordSegura123!", minLength = 8)
        @NotBlank @Size(min = 8) String password,

        @Schema(description = "URL link to the driver's health booklet", example = "https://example.com/health-booklet.pdf")
        @NotBlank String foodHandlerCertificateUrl,

        @Schema(description = "Date of expiration of the driver's health booklet", example = "31-12-2025")
        @NotNull LocalDate foodHandlerCertificateExpiration,

        @Schema(description = "Vehicle registration data")
        @NotNull @Valid VehicleRegistrationDTO vehicle,

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

        @Schema(description = "Geographical latitude coordinate using WGS 84 GPS standard", example = "-31.4233", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Double latitude,

        @Schema(description = "Geographical longitude coordinate using WGS 84 GPS standard", example = "-64.1865", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Double longitude,

        @Schema(description = "Database unique identifier of the predefined neighborhood", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Long neighborhoodId
) {
}
