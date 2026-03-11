package com.church.anglican.backend.dto.identity;

import lombok.Data;

import java.util.UUID;

@Data
public class ElectionResultSummaryResponse {
    private UUID candidateId;
    private UUID personId;
    private int totalVotes;
}
