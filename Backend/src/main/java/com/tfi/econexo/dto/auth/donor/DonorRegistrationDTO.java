package com.tfi.econexo.dto.auth.donor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DonorRegistrationDTO(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @NotBlank
        String tradeName,

        @NotBlank
        String legalName,

        @NotBlank
        String taxId,

        @NotBlank
        String phoneNumber,

        @NotBlank
        String street,

        @NotBlank
        String streetNumber,

        Integer floor,
        String apartment,

        @NotBlank
        String donorType,

        @NotNull
        Double latitude,

        @NotNull
        Double longitude,

        @NotNull
        Long neighborhoodId
)
{}
