package com.church.anglican.backend.entities.identity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "membership_status_history")
@Data
public class MembershipStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id", nullable = false)
    private ChurchMembership membership;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChurchMembership.MembershipStatus status;

    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_person_id")
    private Person changedBy;

    private LocalDateTime effectiveDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
