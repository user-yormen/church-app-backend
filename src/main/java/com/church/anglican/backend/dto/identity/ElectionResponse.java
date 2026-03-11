package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.Election;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ElectionResponse {
    private UUID id;
    private UUID churchId;
    private Election.ElectionScope scopeType;
    private UUID scopeId;
    private UUID roleId;
    private String title;
    private String description;
    private LocalDateTime nominationStart;
    private LocalDateTime nominationEnd;
    private LocalDateTime votingStart;
    private LocalDateTime votingEnd;
    private Election.ElectionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
