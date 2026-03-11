package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.dto.identity.CreateAppRoleRequest;
import com.church.anglican.backend.entities.identity.AppRole;
import com.church.anglican.backend.entities.identity.Church;
import com.church.anglican.backend.entities.identity.Permission;
import com.church.anglican.backend.exception.NotFoundException;
import com.church.anglican.backend.repositories.identity.AppRoleRepository;
import com.church.anglican.backend.repositories.identity.ChurchRepository;
import com.church.anglican.backend.repositories.identity.PermissionRepository;
import com.church.anglican.backend.services.identity.AppRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AppRoleServiceImpl implements AppRoleService {

    private final AppRoleRepository appRoleRepository;
    private final ChurchRepository churchRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public AppRoleServiceImpl(AppRoleRepository appRoleRepository, ChurchRepository churchRepository, PermissionRepository permissionRepository) {
        this.appRoleRepository = appRoleRepository;
        this.churchRepository = churchRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public AppRole createRole(CreateAppRoleRequest createRoleRequest) {
        Church church = churchRepository.findById(createRoleRequest.getChurchId())
                .orElseThrow(() -> new NotFoundException("Church not found with id: " + createRoleRequest.getChurchId()));

        AppRole role = new AppRole();
        role.setName(createRoleRequest.getName());
        role.setDescription(createRoleRequest.getDescription());
        role.setIdentifier(createRoleRequest.getIdentifier());
        role.setChurch(church);

        if (createRoleRequest.getParentRoleId() != null) {
            AppRole parentRole = appRoleRepository.findById(createRoleRequest.getParentRoleId())
                    .orElseThrow(() -> new NotFoundException("Parent role not found with id: " + createRoleRequest.getParentRoleId()));
            role.setParentRole(parentRole);
        }

        if (createRoleRequest.getPermissionIds() != null && !createRoleRequest.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(createRoleRequest.getPermissionIds()));
            role.setPermissions(permissions);
        }

        return appRoleRepository.save(role);
    }

    @Override
    public AppRole findById(UUID id) {
        return appRoleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + id));
    }

    @Override
    public List<AppRole> findByChurchId(UUID churchId) {
        return appRoleRepository.findAllByChurchIdOrderByNameAsc(churchId);
    }
}
