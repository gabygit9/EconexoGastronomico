package com.tfi.econexo.dtos.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DonorRegistrationDTO(
        @NotNull
        @Email
        String email,

        @NotNull
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @JsonProperty("business_name")
        @NotNull
        String businessName,

        @JsonProperty("legal_name")
        String legalName,

        @JsonProperty("tax_identification")
        @NotNull
        String taxIdentification,

        @NotNull
        String phone,

        @NotNull
        String street,

        @JsonProperty("street_number")
        @NotNull
        String streetNumber,

        @NotNull
        Double latitude,

        @NotNull
        Double longitude,

        @JsonProperty("neighborhood_id")
        @NotNull
        Long neighborhoodId,

        @JsonProperty("donor_type")
        @NotNull
        String donorType
)
{}
