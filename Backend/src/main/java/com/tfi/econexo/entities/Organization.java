package com.tfi.econexo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Organization extends BaseEntity{

    @JsonProperty("organization_name")
    @Column(nullable = false)
    private String organizationName;

    @JsonProperty("responsible_name")
    @Column(nullable = false)
    private String responsibleName;

    @Column(unique = true)
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

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonProperty("organization_type")
    @Enumerated(EnumType.STRING)
    private OrganizationType organizationType;
}
