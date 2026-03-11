package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.ElectionCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ElectionCandidateRepository extends JpaRepository<ElectionCandidate, UUID> {
    Page<ElectionCandidate> findByElectionId(UUID electionId, Pageable pageable);

    Optional<ElectionCandidate> findByElectionIdAndPersonId(UUID electionId, UUID personId);
}
