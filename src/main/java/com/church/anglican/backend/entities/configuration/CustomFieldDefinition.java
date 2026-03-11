package com.church.anglican.backend.entities.configuration;

import com.church.anglican.backend.entities.identity.Church;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "custom_field_definitions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"church_id", "name", "appliesTo"})
})
@Data
public class CustomFieldDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id", nullable = false)
    private Church church;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FieldType type;

    @Column(columnDefinition = "TEXT")
    private String options; // For dropdowns

    private boolean required;

    @Column(nullable = false)
    private String appliesTo; // e.g., "Person"

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

enum FieldType {
    TEXT,
    NUMBER,
    DATE,
    DROPDOWN
}
