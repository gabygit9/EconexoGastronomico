package com.tfi.econexo.utils.validation;

import com.tfi.econexo.dto.auth.logistics.DriverRegistrationDTO;
import com.tfi.econexo.dto.auth.logistics.VehicleRegistrationDTO;
import com.tfi.econexo.model.logistics.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehicleRequirementsValidatorTest {

    private VehicleRequirementsValidator validator;

    @BeforeEach
    void setUp() {
        validator = new VehicleRequirementsValidator();
    }

    @Test
    void isValid_WhenVehicleTypeIsBicycle_ThenReturnTrue() {
        VehicleRegistrationDTO vehicleDto = new VehicleRegistrationDTO(
                VehicleType.BICYCLE, true, 0, null, null, null, null
        );
        DriverRegistrationDTO driverDto = buildDriverDto(vehicleDto);

        assertTrue(validator.isValid(driverDto, null));
    }

    @Test
    void isValid_WhenVehicleTypeIsCarWithAllData_ThenReturnTrue() {
        VehicleRegistrationDTO vehicleDto = new VehicleRegistrationDTO(
                VehicleType.CAR, true, 1000, "AA123CC",
                "www.front.com", "www.back.com", LocalDate.of(2029, 4, 12)
        );
        DriverRegistrationDTO driverDto = buildDriverDto(vehicleDto);

        assertTrue(validator.isValid(driverDto, null));
    }

    @Test
    void isValid_WhenVehicleHasEngine_AndMissingNumberPlate_ThenReturnFalse() {
        VehicleRegistrationDTO vehicleDto = new VehicleRegistrationDTO(
                VehicleType.CAR, true, 1000, null,
                "www.front.com", "www.back.com", LocalDate.of(2029, 4, 12)
        );
        DriverRegistrationDTO driverDto = buildDriverDto(vehicleDto);

        assertFalse(validator.isValid(driverDto, null));
    }

    private DriverRegistrationDTO buildDriverDto(VehicleRegistrationDTO vehicleDto) {
        return new DriverRegistrationDTO(
                "Ana", "Perez", "235039876522",
                LocalDate.of(1990, 4, 12), "ana@mail.com", "Pass123",
                "www.url.com", LocalDate.of(2027, 5, 30),
                vehicleDto, "354325543223", "Obispo Trejo",
                "440", "PB", "A", -32.00393, -64.999332, 1L
        );
    }
}