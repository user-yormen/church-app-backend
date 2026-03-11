package com.church.anglican.backend.entities.identity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "person_app_roles")
@Data
public class PersonRole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_role_id", nullable = false)
    private AppRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleScope scopeType;

    @Column(name = "scope_id", nullable = false)
    private UUID scopeId;

    @Column(nullable = false)
    private LocalDateTime assignedDate;

    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentSource source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum RoleScope {
        CHURCH,
        GROUP
    }

    public enum AssignmentSource {
        ELECTION,
        APPOINTMENT
    }

    public enum AssignmentStatus {
        ACTIVE,
        INACTIVE,
        EXPIRED
    }
}
