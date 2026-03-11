package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.dto.identity.AssignPersonRoleRequest;
import com.church.anglican.backend.entities.identity.PersonRole;

public interface PersonRoleManagementService {
    PersonRole assignRole(AssignPersonRoleRequest request);
}
