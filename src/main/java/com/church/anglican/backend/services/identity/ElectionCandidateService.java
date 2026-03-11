package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.entities.identity.ElectionCandidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ElectionCandidateService {
    ElectionCandidate addCandidate(UUID electionId, UUID personId);
    Page<ElectionCandidate> listCandidates(UUID electionId, Pageable pageable);
}
