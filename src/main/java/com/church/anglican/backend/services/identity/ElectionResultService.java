package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.dto.identity.ElectionResultSummaryResponse;
import com.church.anglican.backend.entities.identity.ElectionResult;

import java.util.List;
import java.util.UUID;

public interface ElectionResultService {
    List<ElectionResultSummaryResponse> summarize(UUID electionId);
    List<ElectionResult> persistResults(UUID electionId, UUID winnerPersonId);
}
