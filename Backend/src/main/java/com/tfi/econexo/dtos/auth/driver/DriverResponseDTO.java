package com.tfi.econexo.dtos.auth.driver;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DriverResponseDTO(
        Long id,

        @JsonProperty("first_name")
        String firstName,

        @JsonProperty("last_name")
        String lastName,

        @JsonProperty("tax_identification")
        String taxIdentification,

        @JsonProperty("vehicle_type")
        String vehicleType,

        @JsonProperty("number_plate")
        String numberPlate,

        @JsonProperty("has_refrigeration")
        boolean hasRefrigeration,

        @JsonProperty("neighborhood_id")
        Long neighborhoodId,

        Integer capacity
) {
}
