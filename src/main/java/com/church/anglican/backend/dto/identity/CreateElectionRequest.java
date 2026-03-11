package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.Election;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateElectionRequest {

    @NotNull(message = "Church ID is required")
    private UUID churchId;

    @NotNull(message = "Scope type is required")
    private Election.ElectionScope scopeType;

    @NotNull(message = "Scope ID is required")
    private UUID scopeId;

    @NotNull(message = "Role ID is required")
    private UUID roleId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Nomination start is required")
    private LocalDateTime nominationStart;

    @NotNull(message = "Nomination end is required")
    private LocalDateTime nominationEnd;

    @NotNull(message = "Voting start is required")
    private LocalDateTime votingStart;

    @NotNull(message = "Voting end is required")
    private LocalDateTime votingEnd;

    @NotNull(message = "Status is required")
    private Election.ElectionStatus status;
}
