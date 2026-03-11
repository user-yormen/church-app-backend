package com.church.anglican.backend.entities.financial;

import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.entities.identity.AppRole;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "collection_confirmations")
@Data
public class CollectionConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_event_id", nullable = false)
    private CollectionEvent collectionEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person confirmingPerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private AppRole roleAtTimeOfConfirmation;

    @Column(nullable = false)
    private LocalDateTime confirmationTimestamp;

    private String confirmationLevel; // e.g., "SUPERVISOR_CONFIRMATION", "FINANCE_APPROVAL"

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
