package com.church.anglican.backend.services.financial;

import com.church.anglican.backend.dto.financial.AddCountingParticipantRequest;
import com.church.anglican.backend.entities.financial.CountingParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CountingParticipantService {
    CountingParticipant add(UUID countingSessionId, AddCountingParticipantRequest request);
    Page<CountingParticipant> list(UUID countingSessionId, Pageable pageable);
}
