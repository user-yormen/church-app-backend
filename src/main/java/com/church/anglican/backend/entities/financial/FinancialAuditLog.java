package com.church.anglican.backend.entities.financial;

import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.entities.identity.AppRole;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "financial_audit_logs")
@Data
public class FinancialAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person actor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private AppRole roleAtTimeOfAction;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String action; // e.g., "CREATE_COLLECTION_EVENT", "CONFIRM_COUNT"

    private String entityType; // e.g., "CollectionEvent"
    private UUID entityId;

    @Column(columnDefinition = "TEXT")
    private String previousValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;
}
