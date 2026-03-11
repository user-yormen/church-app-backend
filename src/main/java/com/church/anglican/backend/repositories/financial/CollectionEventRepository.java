package com.church.anglican.backend.repositories.financial;

import com.church.anglican.backend.entities.financial.CollectionEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Repository
public interface CollectionEventRepository extends JpaRepository<CollectionEvent, UUID> {
    Page<CollectionEvent> findByChurchId(UUID churchId, Pageable pageable);
    Page<CollectionEvent> findByChurchIdAndStatus(UUID churchId, CollectionEvent.CollectionEventStatus status, Pageable pageable);
}
