package com.church.anglican.backend.entities.configuration;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "custom_field_values")
@Data
public class CustomFieldValue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "definition_id", nullable = false)
    private CustomFieldDefinition definition;

    @Column(nullable = false)
    private UUID entityId; // The ID of the entity instance, e.g., a Person's ID.

    @Column(name = "field_value", columnDefinition = "TEXT")
    private String fieldValue;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
