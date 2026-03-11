package com.church.anglican.backend.entities.financial;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "collection_amount_breakdowns")
@Data
public class CollectionAmountBreakdown {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counting_session_id", nullable = false)
    private CountingSession countingSession;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    private BigDecimal notesAmount;
    private BigDecimal coinsAmount;
    private BigDecimal chequesAmount;
    private BigDecimal transfersAmount;

    @Column(columnDefinition = "TEXT")
    private String denominationBreakdown; // e.g., JSON: {"100": 5, "50": 10}

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

