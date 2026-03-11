package com.church.anglican.backend.dto.financial;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateCollectionAmountBreakdownRequest {

    @NotNull(message = "Counting session ID is required")
    private UUID countingSessionId;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.00", inclusive = false, message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    @DecimalMin(value = "0.00", message = "Notes amount must be >= 0")
    private BigDecimal notesAmount;
    @DecimalMin(value = "0.00", message = "Coins amount must be >= 0")
    private BigDecimal coinsAmount;
    @DecimalMin(value = "0.00", message = "Cheques amount must be >= 0")
    private BigDecimal chequesAmount;
    @DecimalMin(value = "0.00", message = "Transfers amount must be >= 0")
    private BigDecimal transfersAmount;

    private String denominationBreakdown;

    @NotNull(message = "Actor person ID is required")
    private UUID actorPersonId;

    @NotNull(message = "Actor role ID is required")
    private UUID actorRoleId;
}
