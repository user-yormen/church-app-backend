package com.church.anglican.backend.dto.identity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class FinalizeElectionRequest {
    @NotNull(message = "Winner person ID is required")
    private UUID winnerPersonId;
}
