package com.tfi.econexo.entities.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Abstract base class providing common auditing fields for all entities.
 * The {@code BaseEntity} class defines metadata attributes automatically
 * managed by Spring Data JPA’s auditing infrastructure. It captures
 * creation and modification timestamps as well as the identifiers
 * of the users responsible for those actions.
 * */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * Represents the primary key identifier for the entity. This field is unique and
     * is automatically generated using the `GenerationType.IDENTITY` strategy.
     * It serves as a fundamental property for distinguishing individual records in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * Represents the version field used for optimistic locking to ensure consistency
     * and concurrency control when updating entity records. This field is automatically
     * managed by the JPA provider and incremented with each update operation performed
     * on the entity.
     */
    @Version
    protected Long version;

    /**
     * Represents the timestamp when the entity was created.
     * This field is automatically populated when a new record is inserted into
     * the database. It is marked as non-updatable to ensure the original
     * creation time remains intact.
     */
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty("created_date")
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    protected LocalDateTime createdDate;

    /**
     * Represents the timestamp of the last update made to the entity.
     * This field is automatically managed by JPA and is updated to the current
     * timestamp each time an update operation is performed on the entity.
     */
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty("updated_date")
    @LastModifiedDate
    @Column(name = "updated_date", nullable = false)
    protected LocalDateTime updatedDate;

    /**
     * Represents the user responsible for creating the entity.
     * This field is mandatory and cannot be updated after the entity
     * has been created. It serves as an audit field to track the
     * origin of the entity within the system.
     */
    @JsonProperty("created_by")
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    protected Long createdBy;

    /**
     * Represents the identifier of the last user who updated this entity.
     * This field is mandatory and is used to track the user responsible for the most
     * recent update, facilitating auditability and change tracking.
     * The value of this field is expected to be a non-null string indicating
     * the unique identifier of the user.
     */
    @JsonProperty("updated_by")
    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    protected Long updatedBy;

    private boolean isActive = true;
}
