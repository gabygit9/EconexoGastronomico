package com.tfi.econexo.dtos.auth.organization;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrganizationRegistrationDTO(
        @NotNull
        @Email
        String email,

        @NotNull
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @JsonProperty("organization_name")
        @NotNull
        String organizationName,

        @JsonProperty("responsible_name")
        String responsibleName,

        @NotNull
        String cuit,

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

        @JsonProperty("organization_type")
        @NotNull
        String organizationType
) {
}
