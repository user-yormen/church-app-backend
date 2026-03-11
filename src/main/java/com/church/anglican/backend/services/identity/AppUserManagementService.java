package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.dto.identity.CreateAppUserRequest;
import com.church.anglican.backend.entities.identity.AppUser;

public interface AppUserManagementService {
    AppUser createUser(CreateAppUserRequest request);
}
