package com.church.anglican.backend.services.financial;

import com.church.anglican.backend.dto.financial.CreateCollectionAmountBreakdownRequest;
import com.church.anglican.backend.entities.financial.CollectionAmountBreakdown;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CollectionAmountBreakdownService {
    CollectionAmountBreakdown create(CreateCollectionAmountBreakdownRequest request);
    Page<CollectionAmountBreakdown> list(UUID countingSessionId, Pageable pageable);
}
