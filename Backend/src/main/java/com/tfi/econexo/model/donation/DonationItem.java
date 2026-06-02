package com.tfi.econexo.model.donation;

import com.tfi.econexo.model.base.BaseEntity;
import com.tfi.econexo.model.donation.catalog.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "donation_items")
public class DonationItem extends BaseEntity {

    @Column(nullable = false)
    private Double quantity;

    @Column(name = "batch_number")
    private String batchNumber;

    @Column(name = "donation_date")
    private LocalDateTime donationDate;

    @Column(name = "production_date")
    private LocalDateTime productionDate;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    @Column(name = "delivery_temperature")
    private String deliveryTemperature;

    @Column(name = "allergen_warning")
    private String allergenWarning;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
