package com.church.anglican.backend.services.financial;

import com.church.anglican.backend.dto.financial.CreateCountingSessionRequest;
import com.church.anglican.backend.entities.financial.CountingSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CountingSessionService {
    CountingSession create(CreateCountingSessionRequest request);
    CountingSession findById(UUID id);
    Page<CountingSession> list(UUID collectionEventId, Pageable pageable);
    CountingSession update(UUID id, CreateCountingSessionRequest request);
    void delete(UUID id);
}
