package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.PersonRole;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PersonRoleResponse {
    private UUID id;
    private UUID personId;
    private UUID roleId;
    private UUID churchId;
    private PersonRole.RoleScope scopeType;
    private UUID scopeId;
    private LocalDateTime assignedDate;
    private LocalDateTime expiryDate;
    private PersonRole.AssignmentSource source;
    private PersonRole.AssignmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
