package com.tfi.econexo.entities.logistics;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tfi.econexo.entities.base.BaseEntity;
import com.tfi.econexo.entities.location.Neighborhood;
import com.tfi.econexo.entities.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "drivers")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Driver extends BaseEntity {

    @JsonProperty("first_name")
    @Column(nullable = false)
    private String firstName;

    @JsonProperty("last_name")
    @Column(nullable = false)
    private String lastName;

    @JsonProperty("license_driver")
    private String licenseDriver;

    private String phone;

    @JsonProperty("tax_identification")
    @Column(unique = true, nullable = false)
    private String taxIdentification;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @JsonProperty("date_of_birth")
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point currentLocation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "neighborhood_id", nullable = false)
    private Neighborhood neighborhood;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();
}
