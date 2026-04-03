package com.church.anglican.backend.controllers;

import com.church.anglican.backend.dto.identity.AppUserResponse;
import com.church.anglican.backend.dto.identity.CreateAppUserRequest;
import com.church.anglican.backend.entities.identity.AppRole;
import com.church.anglican.backend.entities.identity.AppUser;
import com.church.anglican.backend.services.identity.AppUserManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class AppUserManagementController {

    private final AppUserManagementService appUserManagementService;

    public AppUserManagementController(AppUserManagementService appUserManagementService) {
        this.appUserManagementService = appUserManagementService;
    }

    @PostMapping
    public ResponseEntity<AppUserResponse> create(@Valid @RequestBody CreateAppUserRequest request) {
        AppUser user = appUserManagementService.createUser(request);
        return new ResponseEntity<>(toResponse(user), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AppUserResponse>> list(@RequestParam UUID churchId) {
        return ResponseEntity.ok(appUserManagementService.listUsers(churchId).stream().map(this::toResponse).toList());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<AppUserResponse> update(
            @PathVariable UUID userId,
            @RequestBody UpdateAppUserRequest request
    ) {
        AppUser user = appUserManagementService.updateUser(userId, request.enabled(), request.roleIds());
        return ResponseEntity.ok(toResponse(user));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        appUserManagementService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    private AppUserResponse toResponse(AppUser user) {
        AppUserResponse response = new AppUserResponse();
        response.setUserId(user.getId());
        response.setPersonId(user.getPerson() != null ? user.getPerson().getId() : null);
        response.setChurchId(user.getRoles().stream()
                .map(AppRole::getChurch)
                .filter(church -> church != null)
                .map(church -> church.getId())
                .findFirst()
                .orElse(null));
        response.setPersonName(user.getPerson() != null ? user.getPerson().getFullName() : null);
        response.setEmailAddress(user.getPerson() != null ? user.getPerson().getEmailAddress() : null);
        response.setPhoneNumber(user.getPerson() != null ? user.getPerson().getPhoneNumber() : null);
        response.setUsername(user.getUsername());
        response.setEnabled(user.isEnabled());
        response.setRoles(user.getRoles().stream()
                .map(AppRole::getName)
                .sorted()
                .toList());
        response.setRoleIdentifiers(user.getRoles().stream()
                .map(AppRole::getIdentifier)
                .filter(identifier -> identifier != null && !identifier.isBlank())
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList());
        return response;
    }

    public record UpdateAppUserRequest(boolean enabled, Set<UUID> roleIds) {
    }
}
