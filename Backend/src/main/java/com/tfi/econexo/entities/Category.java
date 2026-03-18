package com.tfi.econexo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @JsonProperty("requires_refrigeration")
    @Column(nullable = false)
    private boolean requiresRefrigeration = false;

    @JsonProperty("is_packaged_at_origin")
    @Column(nullable = false)
    private boolean isPackagedAtOrigin = false;
}
