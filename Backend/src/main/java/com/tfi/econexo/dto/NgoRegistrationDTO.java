package com.tfi.econexo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NgoRegistrationDTO(
        @NotBlank String ngoName,
        @NotBlank String taxId,
        @NotBlank String legalPersonalityNumber,
        @NotBlank String responsibleName,
        @NotBlank String street,
        @NotBlank String streetNumber,
        String floor,
        String apartment,
        @NotBlank String phoneNumber,
        @NotNull Long neighborhoodId,
        @NotNull Double latitude,
        @NotNull Double longitude,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String ngoType
) {
}
