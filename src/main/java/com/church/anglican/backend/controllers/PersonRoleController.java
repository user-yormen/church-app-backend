package com.church.anglican.backend.controllers;

import com.church.anglican.backend.dto.identity.AssignPersonRoleRequest;
import com.church.anglican.backend.dto.identity.PersonRoleResponse;
import com.church.anglican.backend.entities.identity.PersonRole;
import com.church.anglican.backend.services.identity.PersonRoleManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/person-roles")
public class PersonRoleController {

    private final PersonRoleManagementService personRoleManagementService;

    public PersonRoleController(PersonRoleManagementService personRoleManagementService) {
        this.personRoleManagementService = personRoleManagementService;
    }

    @PostMapping
    public ResponseEntity<PersonRoleResponse> assign(@Valid @RequestBody AssignPersonRoleRequest request) {
        PersonRole personRole = personRoleManagementService.assignRole(request);
        return new ResponseEntity<>(toResponse(personRole), HttpStatus.CREATED);
    }

    private PersonRoleResponse toResponse(PersonRole personRole) {
        PersonRoleResponse response = new PersonRoleResponse();
        response.setId(personRole.getId());
        response.setPersonId(personRole.getPerson() != null ? personRole.getPerson().getId() : null);
        response.setRoleId(personRole.getRole() != null ? personRole.getRole().getId() : null);
        response.setChurchId(personRole.getRole() != null && personRole.getRole().getChurch() != null
                ? personRole.getRole().getChurch().getId()
                : null);
        response.setScopeType(personRole.getScopeType());
        response.setScopeId(personRole.getScopeId());
        response.setAssignedDate(personRole.getAssignedDate());
        response.setExpiryDate(personRole.getExpiryDate());
        response.setSource(personRole.getSource());
        response.setStatus(personRole.getStatus());
        response.setCreatedAt(personRole.getCreatedAt());
        response.setUpdatedAt(personRole.getUpdatedAt());
        return response;
    }
}
