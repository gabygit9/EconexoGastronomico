package com.tfi.econexo.model.donation;

import com.tfi.econexo.model.enums.RegistrationStatus;
import com.tfi.econexo.model.base.BaseEntity;
import com.tfi.econexo.model.location.Neighborhood;
import com.tfi.econexo.model.auth.UserSec;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "donors")
public class Donor extends BaseEntity {

    @Column(name = "trade_name", nullable = false)
    private String tradeName;

    @Column(name = "legal_name", nullable = false)
    private String legalName;

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
    @Column(name = "donor_type")
    private DonorType donorType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RegistrationStatus status = RegistrationStatus.APPROVED;
}
