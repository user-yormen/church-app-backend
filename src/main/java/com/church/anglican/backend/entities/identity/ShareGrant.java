package com.church.anglican.backend.entities.identity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "share_grants", indexes = {
    @Index(name = "idx_share_grant_resource", columnList = "resourceType, resourceId"),
    @Index(name = "idx_share_grant_owner", columnList = "owner_church_id"),
    @Index(name = "idx_share_grant_grantee", columnList = "grantee_church_id")
})
@Data
public class ShareGrant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String resourceType; // e.g., "PERSON", "ACHIEVEMENT", "FINANCE_SUMMARY"

    @Column(nullable = false)
    private UUID resourceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_church_id", nullable = false)
    private Church ownerChurch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grantee_church_id", nullable = false)
    private Church granteeChurch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessLevel accessLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_person_id", nullable = false)
    private Person createdBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum AccessLevel {
        READ,
        WRITE
    }
}
