package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.entities.identity.Election;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ElectionService {
    Election create(Election election);
    Election findById(UUID id);
    Page<Election> list(UUID churchId, Election.ElectionStatus status, Election.ElectionScope scopeType, UUID scopeId, UUID roleId, Pageable pageable);
    Election finalizeElection(UUID electionId, UUID winnerPersonId);
}
