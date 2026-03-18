package com.tfi.econexo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends BaseEntity {

    @JsonProperty("number_plate")
    @Column(unique = true)
    private String numberPlate;

    @JsonProperty("has_refrigeration")
    private boolean hasRefrigeration;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @JsonProperty("capacity_kg")
    private int capacityKg;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;
}
