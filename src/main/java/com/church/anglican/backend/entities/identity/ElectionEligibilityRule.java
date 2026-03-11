package com.church.anglican.backend.entities.identity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "election_eligibility_rules")
@Data
public class ElectionEligibilityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleType ruleType;

    @Column(columnDefinition = "TEXT")
    private String ruleConfig; // JSON payload with parameters

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum RuleType {
        DUES_PAID,
        MARRIED,
        MEMBER_DURATION_DAYS,
        AGE_AT_LEAST,
        HAS_ROLE
    }
}
