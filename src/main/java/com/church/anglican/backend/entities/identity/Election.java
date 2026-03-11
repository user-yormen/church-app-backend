package com.church.anglican.backend.entities.identity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "elections")
@Data
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id", nullable = false)
    private Church church;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ElectionScope scopeType;

    @Column(name = "scope_id", nullable = false)
    private UUID scopeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private AppRole roleToAssign;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime nominationStart;

    @Column(nullable = false)
    private LocalDateTime nominationEnd;

    @Column(nullable = false)
    private LocalDateTime votingStart;

    @Column(nullable = false)
    private LocalDateTime votingEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ElectionStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum ElectionScope {
        CHURCH,
        GROUP
    }

    public enum ElectionStatus {
        DRAFT,
        NOMINATION,
        VOTING,
        CLOSED,
        FINALIZED
    }
}
