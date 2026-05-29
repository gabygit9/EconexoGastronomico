package com.tfi.econexo.utils.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = VehicleRequirementsValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidVehicleRequirements {

    String message() default "Lack of vehicle requirements";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
