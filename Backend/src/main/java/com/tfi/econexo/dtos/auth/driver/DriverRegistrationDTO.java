package com.tfi.econexo.dtos.auth.driver;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DriverRegistrationDTO(
        @NotNull
        @Email
        String email,

        @NotNull
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @NotNull
        @JsonProperty("first_name")
        String firstName,

        @NotNull
        @JsonProperty("last_name")
        String lastName,

        @NotNull
        @JsonProperty("license_driver")
        String licenseDriver,

        @JsonProperty("tax_identification")
        @NotNull
        String taxIdentification,

        @NotNull
        String phone,

        @NotNull
        @JsonProperty("date_of_birth")
        LocalDate dateOfBirth,

        @NotNull
        Double latitude,

        @NotNull
        Double longitude,

        @JsonProperty("neighborhood_id")
        @NotNull
        Long neighborhoodId,

        @JsonProperty("number_plate")
        String numberPlate,

        @JsonProperty("has_refrigeration")
        boolean hasRefrigeration,

        @JsonProperty("vehicle_type")
        @NotNull
        String vehicleType,

        @NotNull
        Integer capacity
) {
}
