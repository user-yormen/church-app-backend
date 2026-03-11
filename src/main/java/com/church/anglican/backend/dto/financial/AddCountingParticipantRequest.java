package com.church.anglican.backend.dto.financial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddCountingParticipantRequest {

    @NotNull(message = "Person ID is required")
    private UUID personId;

    @NotNull(message = "Role ID is required")
    private UUID roleId;

    @NotBlank(message = "Participation type is required")
    private String participationType;
}
