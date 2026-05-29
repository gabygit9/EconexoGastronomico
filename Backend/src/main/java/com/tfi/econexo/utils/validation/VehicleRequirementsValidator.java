package com.tfi.econexo.utils.validation;

import com.tfi.econexo.dto.auth.driver.DriverRegistrationDTO;
import com.tfi.econexo.dto.auth.driver.VehicleRegistrationDTO;
import com.tfi.econexo.entity.logistics.VehicleType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VehicleRequirementsValidator implements ConstraintValidator<ValidVehicleRequirements, DriverRegistrationDTO> {

    @Override
    public boolean isValid(DriverRegistrationDTO dto, ConstraintValidatorContext context) {

        if(dto.vehicle() == null || dto.vehicle().vehicleType() == null) return true;

        VehicleType type = dto.vehicle().vehicleType();

        if(type == VehicleType.BICYCLE || type == VehicleType.KICK_SCOOTER) return true;

        VehicleRegistrationDTO vehicle = dto.vehicle();
        boolean isValid = true;

        if(vehicle.numberPlate() == null || vehicle.numberPlate().isEmpty()) return !isValid;
        if(vehicle.driverLicenseFrontUrl() == null || vehicle.driverLicenseBackUrl() == null || vehicle.driverLicenseExpiration() == null) return !isValid;

        return isValid;
    }
}
