package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.PersonRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AssignPersonRoleRequest {

    @NotNull(message = "Person ID is required")
    private UUID personId;

    @NotNull(message = "Role ID is required")
    private UUID roleId;

    @NotNull(message = "Scope type is required")
    private PersonRole.RoleScope scopeType;

    @NotNull(message = "Scope ID is required")
    private UUID scopeId;

    private LocalDateTime assignedDate;
    private LocalDateTime expiryDate;

    @NotNull(message = "Source is required")
    private PersonRole.AssignmentSource source;

    private PersonRole.AssignmentStatus status;
}
