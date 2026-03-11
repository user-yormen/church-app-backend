package com.church.anglican.backend.dto.financial;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CountingParticipantResponse {
    private UUID id;
    private UUID countingSessionId;
    private UUID personId;
    private UUID roleId;
    private String participationType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
