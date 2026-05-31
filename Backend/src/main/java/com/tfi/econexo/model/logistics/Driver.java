package com.tfi.econexo.model.logistics;

import com.tfi.econexo.model.enums.RegistrationStatus;
import com.tfi.econexo.model.base.BaseEntity;
import com.tfi.econexo.model.location.Neighborhood;
import com.tfi.econexo.model.auth.UserSec;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

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

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate birthDate;

    @Column(name = "food_handler_certificate_url")
    private String foodHandlerCertificateUrl;

    @Column(name = "food_handler_certificate_expiration")
    private LocalDate foodHandlerCertificateExpiration;

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
