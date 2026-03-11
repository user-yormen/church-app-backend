package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.ElectionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ElectionResultRepository extends JpaRepository<ElectionResult, UUID> {
    Page<ElectionResult> findByElectionId(UUID electionId, Pageable pageable);

    List<ElectionResult> findByElectionId(UUID electionId);

    void deleteByElectionId(UUID electionId);
}
