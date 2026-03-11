package com.church.anglican.backend.controllers;

import com.church.anglican.backend.dto.auth.BootstrapAdminRequest;
import com.church.anglican.backend.dto.auth.BootstrapAdminResponse;
import com.church.anglican.backend.services.auth.BootstrapAdminService;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@ConditionalOnProperty(name = "app.security.bootstrap.enabled", havingValue = "true")
public class BootstrapAdminController {

    private final BootstrapAdminService bootstrapAdminService;

    public BootstrapAdminController(BootstrapAdminService bootstrapAdminService) {
        this.bootstrapAdminService = bootstrapAdminService;
    }

    @PostMapping("/bootstrap-admin")
    public ResponseEntity<BootstrapAdminResponse> bootstrapAdmin(@Valid @RequestBody BootstrapAdminRequest request) {
        var user = bootstrapAdminService.bootstrapAdmin(request);
        BootstrapAdminResponse response = new BootstrapAdminResponse();
        response.setUserId(user.getId());
        response.setPersonId(user.getPerson().getId());
        response.setUsername(user.getUsername());
        return ResponseEntity.ok(response);
    }
}
