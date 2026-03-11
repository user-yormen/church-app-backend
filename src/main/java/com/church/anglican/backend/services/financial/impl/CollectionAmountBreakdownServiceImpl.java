package com.church.anglican.backend.services.financial.impl;

import com.church.anglican.backend.dto.financial.CreateCollectionAmountBreakdownRequest;
import com.church.anglican.backend.entities.financial.CollectionAmountBreakdown;
import com.church.anglican.backend.entities.financial.CountingSession;
import com.church.anglican.backend.repositories.financial.CollectionAmountBreakdownRepository;
import com.church.anglican.backend.services.financial.CollectionAmountBreakdownService;
import com.church.anglican.backend.services.financial.FinancialAuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CollectionAmountBreakdownServiceImpl implements CollectionAmountBreakdownService {

    private final CollectionAmountBreakdownRepository breakdownRepository;
    private final FinancialAuditLogService auditLogService;

    public CollectionAmountBreakdownServiceImpl(CollectionAmountBreakdownRepository breakdownRepository, FinancialAuditLogService auditLogService) {
        this.breakdownRepository = breakdownRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public CollectionAmountBreakdown create(CreateCollectionAmountBreakdownRequest request) {
        CollectionAmountBreakdown breakdown = new CollectionAmountBreakdown();
        CountingSession session = new CountingSession();
        session.setId(request.getCountingSessionId());
        breakdown.setCountingSession(session);
        breakdown.setTotalAmount(request.getTotalAmount());
        breakdown.setNotesAmount(request.getNotesAmount());
        breakdown.setCoinsAmount(request.getCoinsAmount());
        breakdown.setChequesAmount(request.getChequesAmount());
        breakdown.setTransfersAmount(request.getTransfersAmount());
        breakdown.setDenominationBreakdown(request.getDenominationBreakdown());
        CollectionAmountBreakdown saved = breakdownRepository.save(breakdown);
        auditLogService.log(
                request.getActorPersonId(),
                request.getActorRoleId(),
                "CREATE_COLLECTION_BREAKDOWN",
                "CollectionAmountBreakdown",
                saved.getId(),
                null,
                "totalAmount=" + saved.getTotalAmount()
        );
        return saved;
    }

    @Override
    public Page<CollectionAmountBreakdown> list(UUID countingSessionId, Pageable pageable) {
        return breakdownRepository.findByCountingSessionId(countingSessionId, pageable);
    }
}
