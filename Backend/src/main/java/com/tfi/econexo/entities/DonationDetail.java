package com.tfi.econexo.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "donation_details")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DonationDetail extends BaseEntity{

    @Column(nullable = false)
    @JsonProperty("quantity_kg")
    private Integer quantityKg;

    @Column(length = 255)
    private String observation;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @JsonProperty("expiration_date")
    @Column(nullable = false)
    private LocalDate expirationDate;

    @JsonProperty("allergen_warning")
    @Column(length = 255)
    private String allergenWarning;

    @ManyToOne
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
