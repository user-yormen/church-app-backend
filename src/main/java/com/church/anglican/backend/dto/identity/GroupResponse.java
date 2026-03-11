package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.Group;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class GroupResponse {
    private UUID id;
    private UUID churchId;
    private String name;
    private String description;
    private Group.GroupType type;
    private Group.GroupStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
