package com.tfi.econexo.model.donation;

import com.tfi.econexo.model.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "received_items")
@Builder
public class ReceivedItem extends BaseEntity {

    @ManyToOne
    private DonationItem donationItem;

    private Double receivedQuantity;
}
