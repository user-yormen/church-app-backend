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
@Table(name = "counting_participants")
@Data
public class CountingParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counting_session_id", nullable = false)
    private CountingSession countingSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private AppRole roleAtTimeOfCounting;

    @Column(nullable = false)
    private String participationType; // e.g., "COUNTER", "SUPERVISOR", "WITNESS"

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
