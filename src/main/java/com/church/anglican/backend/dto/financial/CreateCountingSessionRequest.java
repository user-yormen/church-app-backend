package com.church.anglican.backend.dto.financial;

import com.church.anglican.backend.entities.financial.CountingSession;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateCountingSessionRequest {

    @NotNull(message = "Collection event ID is required")
    private UUID collectionEventId;

    @NotNull(message = "Counting method is required")
    private CountingSession.CountingMethod countingMethod;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NotNull(message = "Status is required")
    private CountingSession.CountingStatus status;

    @NotNull(message = "Actor person ID is required")
    private UUID actorPersonId;

    @NotNull(message = "Actor role ID is required")
    private UUID actorRoleId;
}
