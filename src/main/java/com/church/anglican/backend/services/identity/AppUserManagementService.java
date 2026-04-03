package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.dto.identity.CreateAppUserRequest;
import com.church.anglican.backend.entities.identity.AppUser;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AppUserManagementService {
    AppUser createUser(CreateAppUserRequest request);
    List<AppUser> listUsers(UUID churchId);
    AppUser updateUser(UUID userId, boolean enabled, Set<UUID> roleIds);
    void deleteUser(UUID userId);
}
