package com.tfi.econexo.entities.donation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tfi.econexo.entities.base.BaseEntity;
import com.tfi.econexo.entities.logistics.Driver;
import com.tfi.econexo.entities.organization.Organization;
import com.tfi.econexo.entities.logistics.Vehicle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "donations")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Donation extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationState state;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty("start_date_time")
    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty("end_date_time")
    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @ManyToOne
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @OneToMany(mappedBy = "donation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DonationDetail> details = new ArrayList<>();
}
