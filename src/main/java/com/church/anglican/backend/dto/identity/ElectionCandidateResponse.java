package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.ElectionCandidate;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ElectionCandidateResponse {
    private UUID id;
    private UUID electionId;
    private UUID personId;
    private ElectionCandidate.CandidateStatus status;
    private LocalDateTime createdAt;
}
