package com.church.anglican.backend.repositories.financial;

import com.church.anglican.backend.entities.financial.CountingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Repository
public interface CountingSessionRepository extends JpaRepository<CountingSession, UUID> {
    Page<CountingSession> findByCollectionEventId(UUID collectionEventId, Pageable pageable);
}
