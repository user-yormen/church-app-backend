package com.church.anglican.backend.dto.financial;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CollectionAmountBreakdownResponse {
    private UUID id;
    private UUID countingSessionId;
    private BigDecimal totalAmount;
    private BigDecimal notesAmount;
    private BigDecimal coinsAmount;
    private BigDecimal chequesAmount;
    private BigDecimal transfersAmount;
    private String denominationBreakdown;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
