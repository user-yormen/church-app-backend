package com.church.anglican.backend.dto.identity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CastVoteRequest {
    @NotNull(message = "Voter person ID is required")
    private UUID voterPersonId;

    @NotNull(message = "Candidate ID is required")
    private UUID candidateId;
}
