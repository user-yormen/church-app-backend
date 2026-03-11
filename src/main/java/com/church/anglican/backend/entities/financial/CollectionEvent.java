package com.church.anglican.backend.entities.financial;

import com.church.anglican.backend.entities.configuration.CollectionType;
import com.church.anglican.backend.entities.identity.Church;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "collection_events")
@Data
public class CollectionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_type_id", nullable = false)
    private CollectionType collectionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id", nullable = false)
    private Church church;

    private String serviceReference;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    private String location;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollectionEventStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum CollectionEventStatus {
        OPEN,
        COUNTING,
        SUBMITTED,
        CONFIRMED,
        LOCKED
    }
}
