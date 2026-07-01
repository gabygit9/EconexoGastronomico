package com.tfi.econexo.model.logistics;

import com.tfi.econexo.model.base.BaseEntity;
import com.tfi.econexo.model.donation.Donation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "delivery_evidences")
public class DeliveryEvidence extends BaseEntity {

    @Column(nullable = false)
    private Double temperature;

    @Column(name = "driver_signature_url", nullable = false)
    private String driverSignatureUrl;

    @Column(name = "ngo_signature_url")
    private String ngoSignatureUrl;

    @Column(name = "evidence_photo_url")
    private String evidencePhotoUrl;

    @Column(name = "disclaimer_accepted")
    private boolean disclaimerAccepted;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", unique = true, nullable = false)
    private Donation donation;
}
