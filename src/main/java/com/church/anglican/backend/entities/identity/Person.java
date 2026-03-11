package com.church.anglican.backend.entities.identity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "persons")
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String preferredName;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDateTime dateOfBirth;

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true)
    private String emailAddress;

    private String address;

    private String emergencyContact;

    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum Gender {
        MALE,
        FEMALE,
        OTHER
    }

    public enum PersonStatus {
        ACTIVE,
        INACTIVE,
        DECEASED,
        VISITOR
    }

    public enum MaritalStatus {
        SINGLE,
        MARRIED,
        WIDOWED,
        DIVORCED,
        SEPARATED
    }
}
