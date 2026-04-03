package com.church.anglican.backend.services.financial;

import com.church.anglican.backend.dto.financial.CreateCollectionEventRequest;
import com.church.anglican.backend.entities.financial.CollectionEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CollectionEventService {
    CollectionEvent create(CreateCollectionEventRequest request);
    CollectionEvent findById(UUID id);
    Page<CollectionEvent> list(UUID churchId, CollectionEvent.CollectionEventStatus status, Pageable pageable);
    CollectionEvent update(UUID id, CreateCollectionEventRequest request);
    void delete(UUID id);
}
