package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ElectionRepository extends JpaRepository<Election, UUID>, JpaSpecificationExecutor<Election> {
    Page<Election> findByChurchId(UUID churchId, Pageable pageable);

    Page<Election> findByChurchIdAndStatus(UUID churchId, Election.ElectionStatus status, Pageable pageable);

    Page<Election> findByChurchIdAndScopeTypeAndScopeId(UUID churchId, Election.ElectionScope scopeType, UUID scopeId, Pageable pageable);
}
