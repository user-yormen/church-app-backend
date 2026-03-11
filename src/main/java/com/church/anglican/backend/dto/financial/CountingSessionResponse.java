package com.church.anglican.backend.dto.financial;

import com.church.anglican.backend.entities.financial.CountingSession;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CountingSessionResponse {
    private UUID id;
    private UUID collectionEventId;
    private CountingSession.CountingMethod countingMethod;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private CountingSession.CountingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
