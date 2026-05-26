package com.tfi.econexo.entity.ngo;

import com.tfi.econexo.entity.base.BaseEntity;
import com.tfi.econexo.entity.location.Neighborhood;
import com.tfi.econexo.entity.security.UserSec;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "organizations")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Ngo extends BaseEntity {
    @Column(nullable = false)
    private String ngoName;

    @Column(nullable = false, unique = true, length = 11)
    private String taxId;

    @Column(nullable = false, unique = true)
    private String legalPersonalityNumber;

    @Column(nullable = false)
    private String responsibleName;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String streetNumber;

    @Column(nullable = true)
    private String floor;

    @Column(nullable = true)
    private String apartment;

    @Column(nullable = false)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "neighborhood_id", nullable = false)
    private Neighborhood neighborhood;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserSec user;

    @Enumerated(EnumType.STRING)
    private NgoType ngoType;
}
