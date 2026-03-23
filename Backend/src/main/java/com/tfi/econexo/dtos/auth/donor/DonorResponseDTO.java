package com.tfi.econexo.dtos.auth.donor;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DonorResponseDTO(

        Long id,

        @JsonProperty("business_name")
        String businessName,

        @JsonProperty("legal_name")
        String legalName,

        @JsonProperty("tax_identification")
        String taxIdentification,

        String phone,

        String street,

        @JsonProperty("street_number")
        String streetNumber,

        @JsonProperty("neighborhood_id")
        Long neighborhoodId,

        @JsonProperty("donor_type")
        String donorType
) {
}
