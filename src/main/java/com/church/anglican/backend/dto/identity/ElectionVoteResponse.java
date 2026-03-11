package com.church.anglican.backend.dto.identity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ElectionVoteResponse {
    private UUID id;
    private UUID electionId;
    private UUID voterPersonId;
    private UUID candidateId;
    private LocalDateTime createdAt;
}
