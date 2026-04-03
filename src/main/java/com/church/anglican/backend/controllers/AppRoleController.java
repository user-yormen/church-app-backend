package com.church.anglican.backend.controllers;

import com.church.anglican.backend.dto.identity.AppRoleResponse;
import com.church.anglican.backend.dto.identity.CreateAppRoleRequest;
import com.church.anglican.backend.entities.identity.AppRole;
import com.church.anglican.backend.services.identity.AppRoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
public class AppRoleController {

    private final AppRoleService appRoleService;

    @Autowired
    public AppRoleController(AppRoleService appRoleService) {
        this.appRoleService = appRoleService;
    }

    @PostMapping
    public ResponseEntity<AppRoleResponse> createRole(@Valid @RequestBody CreateAppRoleRequest createRoleRequest) {
        AppRole createdRole = appRoleService.createRole(createRoleRequest);
        return new ResponseEntity<>(toResponse(createdRole), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AppRoleResponse>> listRoles(@RequestParam UUID churchId) {
        return ResponseEntity.ok(appRoleService.findByChurchId(churchId).stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppRoleResponse> getRoleById(@PathVariable UUID id) {
        try {
            AppRole role = appRoleService.findById(id);
            return ResponseEntity.ok(toResponse(role));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppRoleResponse> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody CreateAppRoleRequest request
    ) {
        return ResponseEntity.ok(toResponse(appRoleService.updateRole(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        appRoleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    private AppRoleResponse toResponse(AppRole role) {
        AppRoleResponse response = new AppRoleResponse();
        response.setId(role.getId());
        response.setChurchId(role.getChurch() != null ? role.getChurch().getId() : null);
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        response.setIdentifier(role.getIdentifier());
        response.setParentRoleId(role.getParentRole() != null ? role.getParentRole().getId() : null);
        response.setPermissionIds(role.getPermissions().stream()
                .map(permission -> permission.getId())
                .sorted()
                .toList());
        response.setPermissions(role.getPermissions().stream()
                .map(permission -> permission.getName())
                .sorted()
                .toList());
        return response;
    }
}
