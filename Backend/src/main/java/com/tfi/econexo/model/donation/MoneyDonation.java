package com.tfi.econexo.model.donation;

import com.tfi.econexo.model.base.BaseEntity;
import com.tfi.econexo.model.donation.donor.Donor;
import com.tfi.econexo.model.enums.DonationStatus;
import com.tfi.econexo.model.ngo.Ngo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "money_donations")
public class MoneyDonation extends BaseEntity {

    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "ngo_id")
    private Ngo ngo;

    @ManyToOne
    @JoinColumn(name = "donor_id")
    private Donor donor;

    @Enumerated(EnumType.STRING)
    private DonationStatus status;
}
