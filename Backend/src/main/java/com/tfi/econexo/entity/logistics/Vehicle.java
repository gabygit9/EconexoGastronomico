package com.tfi.econexo.entity.logistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tfi.econexo.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "vehicles")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends BaseEntity {

    @Column(name = "number_plate", unique = true)
    private String numberPlate;

    @Column(name = "has_refrigeration")
    private boolean hasRefrigeration;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    private VehicleType vehicleType;

    @Column(name = "capacity_kg" )
    private int capacityKg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;
    
    @Column(name = "drivers_license_front_url")
    private String driversLicenseFrontUrl;

    @Column(name = "drivers_license_back_url")
    private String driversLicenseBackUrl;

    @Column(name = "drivers_license_expiration")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate driversLicenseExpiration;
}
