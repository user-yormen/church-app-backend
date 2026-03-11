package com.church.anglican.backend.entities.configuration;

import com.church.anglican.backend.entities.identity.Church;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "collection_types", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"church_id", "name"})
})
@Data
public class CollectionType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id", nullable = false)
    private Church church;

    @Column(nullable = false)
    private String name;

    private String description;

    private String defaultFrequency;

    private String expectedCountingMethod;

    private String accountingCategory;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
