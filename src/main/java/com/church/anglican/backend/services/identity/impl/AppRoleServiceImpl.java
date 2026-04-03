package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.dto.identity.CreateAppRoleRequest;
import com.church.anglican.backend.entities.identity.AppRole;
import com.church.anglican.backend.entities.identity.AppUser;
import com.church.anglican.backend.entities.identity.Church;
import com.church.anglican.backend.entities.identity.Permission;
import com.church.anglican.backend.exception.NotFoundException;
import com.church.anglican.backend.repositories.identity.AppRoleRepository;
import com.church.anglican.backend.repositories.identity.AppUserRepository;
import com.church.anglican.backend.repositories.identity.ChurchRepository;
import com.church.anglican.backend.repositories.identity.PermissionRepository;
import com.church.anglican.backend.services.identity.AppRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class AppRoleServiceImpl implements AppRoleService {

    private final AppRoleRepository appRoleRepository;
    private final AppUserRepository appUserRepository;
    private final ChurchRepository churchRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public AppRoleServiceImpl(
            AppRoleRepository appRoleRepository,
            AppUserRepository appUserRepository,
            ChurchRepository churchRepository,
            PermissionRepository permissionRepository
    ) {
        this.appRoleRepository = appRoleRepository;
        this.appUserRepository = appUserRepository;
        this.churchRepository = churchRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public AppRole createRole(CreateAppRoleRequest createRoleRequest) {
        Church church = churchRepository.findById(createRoleRequest.getChurchId())
                .orElseThrow(() -> new NotFoundException("Church not found with id: " + createRoleRequest.getChurchId()));
        validateActorCanManageChurch(requireAuthenticatedUser(), church);

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
        AppRole role = appRoleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + id));
        validateActorCanManageChurch(requireAuthenticatedUser(), role.getChurch());
        return role;
    }

    @Override
    public List<AppRole> findByChurchId(UUID churchId) {
        Church church = churchRepository.findById(churchId)
                .orElseThrow(() -> new NotFoundException("Church not found with id: " + churchId));
        validateActorCanManageChurch(requireAuthenticatedUser(), church);
        return appRoleRepository.findAllByChurchIdOrderByNameAsc(churchId);
    }

    @Override
    public AppRole updateRole(UUID id, CreateAppRoleRequest request) {
        AppRole role = findById(id);
        Church church = churchRepository.findById(request.getChurchId())
                .orElseThrow(() -> new NotFoundException("Church not found with id: " + request.getChurchId()));
        validateActorCanManageChurch(requireAuthenticatedUser(), church);

        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setIdentifier(request.getIdentifier());
        role.setChurch(church);

        if (request.getParentRoleId() != null) {
            AppRole parentRole = appRoleRepository.findById(request.getParentRoleId())
                    .orElseThrow(() -> new NotFoundException("Parent role not found with id: " + request.getParentRoleId()));
            role.setParentRole(parentRole);
        } else {
            role.setParentRole(null);
        }

        if (request.getPermissionIds() != null) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(request.getPermissionIds()));
            role.setPermissions(permissions);
        }

        return appRoleRepository.save(role);
    }

    @Override
    public void deleteRole(UUID id) {
        AppRole role = findById(id);
        validateActorCanManageChurch(requireAuthenticatedUser(), role.getChurch());
        appRoleRepository.delete(role);
    }

    private AppUser requireAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Authenticated user not found"));
    }

    private void validateActorCanManageChurch(AppUser actor, Church church) {
        if (church == null || church.getId() == null) {
            throw new AccessDeniedException("Role is not linked to a church");
        }

        if (hasBackofficeAccess(actor)) {
            return;
        }

        if (!actorChurchesById(actor).containsKey(church.getId())) {
            throw new AccessDeniedException("You can only manage roles for your own church");
        }
    }

    private boolean hasBackofficeAccess(AppUser actor) {
        return actor.getRoles().stream().anyMatch(role ->
                "ADMIN".equalsIgnoreCase(role.getName()) || "BACKOFFICE".equalsIgnoreCase(role.getIdentifier()));
    }

    private Map<UUID, Church> actorChurchesById(AppUser actor) {
        Map<UUID, Church> churches = new LinkedHashMap<>();
        actor.getRoles().stream()
                .map(AppRole::getChurch)
                .filter(church -> church != null && church.getId() != null)
                .forEach(church -> churches.put(church.getId(), church));
        return churches;
    }
}
