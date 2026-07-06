package com.tfi.econexo.model.donation;

import com.tfi.econexo.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "received_donations")
@Builder
public class ReceptionRecord extends BaseEntity {

    @OneToOne
    private Donation donation;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ReceivedItem> items;

    @Column(name = "accepted_disclaimer")
    private boolean acceptedDisclaimer;

    @Column(name = "signature_url")
    private String signatureUrl;

    @Column(name = "acceptance_timestamp")
    private LocalDateTime acceptanceTimestamp;

    @Column(name = "received_by_email")
    private String receivedByEmail;
}
