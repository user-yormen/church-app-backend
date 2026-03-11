package com.church.anglican.backend.entities.financial;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "counting_sessions")
@Data
public class CountingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_event_id", nullable = false)
    private CollectionEvent collectionEvent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CountingMethod countingMethod;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CountingStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum CountingMethod {
        MANUAL,
        RECOUNT,
        VERIFICATION
    }

    public enum CountingStatus {
        IN_PROGRESS,
        SUBMITTED,
        REJECTED,
        CONFIRMED
    }
}
