package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.dto.identity.ElectionEligibilityResponse;

import java.util.UUID;

public interface ElectionEligibilityService {
    ElectionEligibilityResponse evaluate(UUID electionId, UUID personId);
}
