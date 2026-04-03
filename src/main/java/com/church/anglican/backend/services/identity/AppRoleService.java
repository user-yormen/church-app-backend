package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.dto.identity.CreateAppRoleRequest;
import com.church.anglican.backend.entities.identity.AppRole;

import java.util.List;
import java.util.UUID;

public interface AppRoleService {
    AppRole createRole(CreateAppRoleRequest createRoleRequest);
    AppRole findById(UUID id);
    List<AppRole> findByChurchId(UUID churchId);
    AppRole updateRole(UUID id, CreateAppRoleRequest request);
    void deleteRole(UUID id);
}
