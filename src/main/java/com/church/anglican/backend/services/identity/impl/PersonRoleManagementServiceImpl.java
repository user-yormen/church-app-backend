package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.dto.identity.AssignPersonRoleRequest;
import com.church.anglican.backend.entities.identity.AppRole;
import com.church.anglican.backend.entities.identity.AppUser;
import com.church.anglican.backend.entities.identity.Church;
import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.entities.identity.PersonRole;
import com.church.anglican.backend.exception.NotFoundException;
import com.church.anglican.backend.repositories.identity.AppRoleRepository;
import com.church.anglican.backend.repositories.identity.AppUserRepository;
import com.church.anglican.backend.repositories.identity.PersonRepository;
import com.church.anglican.backend.repositories.identity.PersonRoleRepository;
import com.church.anglican.backend.services.identity.PersonRoleManagementService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonRoleManagementServiceImpl implements PersonRoleManagementService {

    private final PersonRoleRepository personRoleRepository;
    private final PersonRepository personRepository;
    private final AppRoleRepository appRoleRepository;
    private final AppUserRepository appUserRepository;

    public PersonRoleManagementServiceImpl(
            PersonRoleRepository personRoleRepository,
            PersonRepository personRepository,
            AppRoleRepository appRoleRepository,
            AppUserRepository appUserRepository
    ) {
        this.personRoleRepository = personRoleRepository;
        this.personRepository = personRepository;
        this.appRoleRepository = appRoleRepository;
        this.appUserRepository = appUserRepository;
    }

    @Override
    public PersonRole assignRole(AssignPersonRoleRequest request) {
        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new NotFoundException("Person not found with id: " + request.getPersonId()));
        AppRole role = appRoleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + request.getRoleId()));
        AppUser actor = requireAuthenticatedUser();
        validateActorCanAssignRole(actor, role, request.getScopeId());

        PersonRole.AssignmentStatus status = request.getStatus() != null
                ? request.getStatus()
                : PersonRole.AssignmentStatus.ACTIVE;

        if (personRoleRepository.existsByPersonIdAndRoleIdAndScopeTypeAndScopeIdAndStatus(
                person.getId(),
                role.getId(),
                request.getScopeType(),
                request.getScopeId(),
                status
        )) {
            throw new IllegalArgumentException("This executive assignment already exists for the selected scope");
        }

        PersonRole personRole = new PersonRole();
        personRole.setPerson(person);
        personRole.setRole(role);
        personRole.setScopeType(request.getScopeType());
        personRole.setScopeId(request.getScopeId());
        personRole.setAssignedDate(request.getAssignedDate() != null ? request.getAssignedDate() : LocalDateTime.now());
        personRole.setExpiryDate(request.getExpiryDate());
        personRole.setSource(request.getSource());
        personRole.setStatus(status);
        return personRoleRepository.save(personRole);
    }

    private AppUser requireAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Authenticated user not found"));
    }

    private void validateActorCanAssignRole(AppUser actor, AppRole role, UUID scopeId) {
        if (hasBackofficeAccess(actor)) {
            return;
        }

        Church roleChurch = role.getChurch();
        if (roleChurch == null || roleChurch.getId() == null) {
            throw new AccessDeniedException("Role must belong to a church");
        }

        Map<UUID, Church> actorChurchsById = actor.getRoles().stream()
                .map(AppRole::getChurch)
                .filter(church -> church != null && church.getId() != null)
                .collect(Collectors.toMap(Church::getId, church -> church, (left, right) -> left));

        if (!actorChurchsById.containsKey(roleChurch.getId())) {
            throw new AccessDeniedException("You can only assign roles within your own church");
        }

        if (scopeId != null && !scopeId.equals(roleChurch.getId())) {
            throw new AccessDeniedException("Church-scoped operators can only assign roles inside their own church scope");
        }
    }

    private boolean hasBackofficeAccess(AppUser actor) {
        return actor.getRoles().stream().anyMatch(role ->
                "ADMIN".equalsIgnoreCase(role.getName()) || "BACKOFFICE".equalsIgnoreCase(role.getIdentifier()));
    }
}
