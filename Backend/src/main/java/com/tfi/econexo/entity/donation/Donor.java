package com.tfi.econexo.entity.donation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tfi.econexo.entity.base.BaseEntity;
import com.tfi.econexo.entity.location.Neighborhood;
import com.tfi.econexo.entity.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "donors")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Donor extends BaseEntity {

    @JsonProperty("business_name")
    @Column(nullable = false)
    private String businessName;

    @JsonProperty("legal_name")
    @Column(nullable = false)
    private String legalName;

    @Column(unique = true, nullable = false)
    private String taxIdentification;

    private String phone;

    @Column(nullable = false)
    private String street;

    @JsonProperty("street_number")
    @Column(nullable = false)
    private String streetNumber;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "neighborhood_id", nullable = false)
    private Neighborhood neighborhood;

    // --- POSTGIS ---
    // SRID 4326 usa el estándar GPS mundial (Latitud/Longitud)
    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @OneToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonProperty("commerce_type")
    @Enumerated(EnumType.STRING)
    private DonorType donorType;
}
