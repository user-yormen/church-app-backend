package com.church.anglican.backend.services.financial.impl;

import com.church.anglican.backend.dto.financial.CreateCountingSessionRequest;
import com.church.anglican.backend.entities.financial.CollectionEvent;
import com.church.anglican.backend.entities.financial.CountingSession;
import com.church.anglican.backend.repositories.financial.CountingSessionRepository;
import com.church.anglican.backend.services.financial.CountingSessionService;
import com.church.anglican.backend.services.financial.FinancialAuditLogService;
import com.church.anglican.backend.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CountingSessionServiceImpl implements CountingSessionService {

    private final CountingSessionRepository countingSessionRepository;
    private final FinancialAuditLogService auditLogService;

    public CountingSessionServiceImpl(CountingSessionRepository countingSessionRepository, FinancialAuditLogService auditLogService) {
        this.countingSessionRepository = countingSessionRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public CountingSession create(CreateCountingSessionRequest request) {
        CountingSession session = new CountingSession();
        CollectionEvent event = new CollectionEvent();
        event.setId(request.getCollectionEventId());
        session.setCollectionEvent(event);
        session.setCountingMethod(request.getCountingMethod());
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());
        session.setStatus(request.getStatus());
        CountingSession saved = countingSessionRepository.save(session);
        auditLogService.log(
                request.getActorPersonId(),
                request.getActorRoleId(),
                "CREATE_COUNTING_SESSION",
                "CountingSession",
                saved.getId(),
                null,
                "status=" + saved.getStatus()
        );
        return saved;
    }

    @Override
    public CountingSession findById(UUID id) {
        return countingSessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Counting session not found with id: " + id));
    }

    @Override
    public Page<CountingSession> list(UUID collectionEventId, Pageable pageable) {
        return countingSessionRepository.findByCollectionEventId(collectionEventId, pageable);
    }
}
