package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.Group;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateGroupRequest {

    @NotNull(message = "Church ID is required")
    private UUID churchId;

    @NotBlank(message = "Group name is required")
    private String name;

    private String description;

    @NotNull(message = "Group type is required")
    private Group.GroupType type;

    private Group.GroupStatus status;
}
