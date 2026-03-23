package com.tfi.econexo.dtos.auth.organization;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrganizationResponseDTO(
        Long id,

        @JsonProperty("organization_name")
        String organizationName,

        @JsonProperty("responsible_name")
        String responsibleName,

        String cuit,

        String phone,

        String street,

        @JsonProperty("street_number")
        String streetNumber,

        @JsonProperty("neighborhood_id")
        Long neighborhoodId,

        @JsonProperty("organization_type")
        String organizationType
) {
}
