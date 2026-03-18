package com.tfi.econexo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Column(unique = true, nullable = false)
    private String cuit;

    private String phone;

    @Column(nullable = false)
    private String street;

    @JsonProperty("street_number")
    @Column(nullable = false)
    private String streetNumber;

    @ManyToOne
    @JoinColumn(name = "neighborhood_id", nullable = false)
    private Neighborhood neighborhood;

    // --- POSTGIS ---
    // SRID 4326 usa el estándar GPS mundial (Latitud/Longitud)
    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonProperty("commerce_type")
    @Enumerated(EnumType.STRING)
    private CommerceType commerceType;
}
