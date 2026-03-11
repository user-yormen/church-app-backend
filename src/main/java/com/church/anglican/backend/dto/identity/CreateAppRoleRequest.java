package com.church.anglican.backend.dto.identity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CreateAppRoleRequest {

    @NotNull(message = "Church ID is required")
    private UUID churchId;

    @NotBlank(message = "Role name is required")
    private String name;

    private String description;

    private String identifier;

    private UUID parentRoleId;

    private Set<UUID> permissionIds;
}
