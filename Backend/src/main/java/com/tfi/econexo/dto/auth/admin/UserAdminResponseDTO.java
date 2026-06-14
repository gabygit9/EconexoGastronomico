package com.tfi.econexo.dto.auth.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "A UserAdminResponseDTO object represents a user in the system.")
public record UserAdminResponseDTO(

        @Schema(description = "The unique identifier of the user.", example = "1L")
        Long userId,

        @Schema(description = "The name of the user.", example = "John Doe")
        String name,

        @Schema(description = "The email of the user.", example = "john.doe@example.com")
        String email,

        @Schema(description = "The role of user.", example = "ADMIN, NGO, DRIVER, DONOR")
        String userType,

        @Schema(description = "The registration status of the use.", example = "PENDING, APPROVED, REJECTED")
        String status,

        @Schema(description = "The date and time the user was created.", example = "2023-01-01T00:00:00")
        LocalDateTime createdDate,

        @Schema(description = "The tax id of the user.", example = "123456789")
        String taxId,

        @Schema(description = "The certificate url to manipulate food.", example = "https://example.com/certificate.pdf")
        String certificateUrl,

        @Schema(description = "The front image of driver license", example = "https://example.com/driver-license-front.png")
        String driversLicenseFrontUrl,

        @Schema(description = "The back image of driver license", example = "https://example.com/driver-license-back.png")
        String driversLicenseBackUrl,

        @Schema(description = "The registration legal number of the ngo.", example = "123456789")
        String registrationNumber
) {
}
