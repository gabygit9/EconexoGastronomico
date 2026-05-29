package com.tfi.econexo.entity.logistics;

import com.tfi.econexo.entity.RegistrationStatus;
import com.tfi.econexo.entity.base.BaseEntity;
import com.tfi.econexo.entity.location.Neighborhood;
import com.tfi.econexo.entity.logistics.Vehicle;
import com.tfi.econexo.entity.security.UserSec;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "drivers")
public class Driver extends BaseEntity {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthDate;

    @Column(name = "drivers_license_front_url")
    private String driversLicenseFrontUrl;

    @Column(name = "drivers_license_back_url")
    private String driversLicenseBackUrl;

    @Column(name = "drivers_license_expiration")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate driversLicenseExpiration;

    @Column(name = "health_booklet_url")
    private String healthBookletUrl;

    @Column(name = "health_booklet_expiration")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate healthBookletExpiration;

    @Column(name = "tax_id", unique = true, nullable = false, length = 11)
    private String taxId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false)
    private String street;

    @Column(name = "street_number", nullable = false)
    private String streetNumber;

    @Column(nullable = true)
    private String floor;

    @Column(nullable = true)
    private String apartment;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "neighborhood_id", nullable = false)
    private Neighborhood neighborhood;

    // --- POSTGIS ---
    // SRID 4326 usa el estándar GPS mundial (Latitud/Longitud)
    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @OneToOne(fetch =  FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private UserSec user;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus status = RegistrationStatus.APPROVED;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();
}
