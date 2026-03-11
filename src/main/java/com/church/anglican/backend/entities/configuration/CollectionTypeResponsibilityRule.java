package com.church.anglican.backend.entities.configuration;

import com.church.anglican.backend.entities.identity.AppRole;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "collection_type_responsibility_rules")
@Data
public class CollectionTypeResponsibilityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_type_id", nullable = false)
    private CollectionType collectionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private AppRole responsibleRole;

    @Column(nullable = false)
    private String responsibilityType; // e.g., "COUNTER", "SUPERVISOR", "CONFIRMER"

    @Column(nullable = false)
    private int requiredCount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
