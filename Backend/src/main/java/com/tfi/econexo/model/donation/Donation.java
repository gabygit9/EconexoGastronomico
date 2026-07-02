package com.tfi.econexo.model.donation;

import com.tfi.econexo.model.base.BaseEntity;
import com.tfi.econexo.model.donation.donor.Donor;
import com.tfi.econexo.model.enums.DonationStatus;
import com.tfi.econexo.model.logistics.DeliveryEvidence;
import com.tfi.econexo.model.logistics.Driver;
import com.tfi.econexo.model.logistics.Vehicle;
import com.tfi.econexo.model.ngo.Ngo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "donations")
@Builder
public class Donation extends BaseEntity {

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationStatus status = DonationStatus.AVAILABLE;

    @Column(name = "pickup_start_time", nullable = false)
    private LocalDateTime pickupStartTime;

    @Column(name = "pickup_end_time", nullable = false)
    private LocalDateTime pickupEndTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ngo_id")
    private Ngo ngo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id")
    private Donor donor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @OneToMany(mappedBy = "donation", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<DonationItem> donationItems = new ArrayList<>();

    @OneToOne(mappedBy = "donation", cascade = {CascadeType.ALL})
    @JoinColumn(name = "delivery_evidence_id", unique = true)
    private DeliveryEvidence deliveryEvidence;

    @Column(name = "reception_comments")
    private String receptionComments;

    public LocalDateTime getMinExpirationDate(){
        if(donationItems == null || donationItems.isEmpty()) return null;
        return donationItems.stream().map(DonationItem::getExpirationDate).min(LocalDateTime::compareTo).orElse(null);
    }

    public boolean isAnyItemRefrigerated(){
        if(donationItems == null || donationItems.isEmpty()) return false;
        return donationItems.stream().anyMatch(item -> item.getProduct() != null && item.getProduct().isRequiresRefrigeration());
    }

}
