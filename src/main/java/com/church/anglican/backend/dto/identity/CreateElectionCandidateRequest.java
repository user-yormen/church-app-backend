package com.church.anglican.backend.dto.identity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateElectionCandidateRequest {
    @NotNull(message = "Person ID is required")
    private UUID personId;
}
