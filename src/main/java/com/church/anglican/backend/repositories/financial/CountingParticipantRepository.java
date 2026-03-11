package com.church.anglican.backend.repositories.financial;

import com.church.anglican.backend.entities.financial.CountingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Repository
public interface CountingParticipantRepository extends JpaRepository<CountingParticipant, UUID> {
    Page<CountingParticipant> findByCountingSessionId(UUID countingSessionId, Pageable pageable);
}
