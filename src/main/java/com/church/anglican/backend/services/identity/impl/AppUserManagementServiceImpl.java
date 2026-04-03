package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.dto.identity.CreateAppUserRequest;
import com.church.anglican.backend.entities.identity.AppRole;
import com.church.anglican.backend.entities.identity.AppUser;
import com.church.anglican.backend.entities.identity.Church;
import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.exception.NotFoundException;
import com.church.anglican.backend.repositories.identity.AppRoleRepository;
import com.church.anglican.backend.repositories.identity.AppUserRepository;
import com.church.anglican.backend.repositories.identity.ChurchRepository;
import com.church.anglican.backend.repositories.identity.PersonRepository;
import com.church.anglican.backend.services.identity.AppUserManagementService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class AppUserManagementServiceImpl implements AppUserManagementService {

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final ChurchRepository churchRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserManagementServiceImpl(
            AppUserRepository appUserRepository,
            AppRoleRepository appRoleRepository,
            ChurchRepository churchRepository,
            PersonRepository personRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.appUserRepository = appUserRepository;
        this.appRoleRepository = appRoleRepository;
        this.churchRepository = churchRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AppUser createUser(CreateAppUserRequest request) {
        appUserRepository.findByUsername(request.getUsername()).ifPresent(existing -> {
            throw new IllegalArgumentException("Username already exists");
        });

        Church church = churchRepository.findById(request.getChurchId())
                .orElseThrow(() -> new NotFoundException("Church not found with id: " + request.getChurchId()));
        AppUser actor = requireAuthenticatedUser();
        validateActorCanManageChurch(actor, church);

        Person person = resolvePerson(request);
        Set<AppRole> roles = resolveRoles(actor, church, request);

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setPerson(person);
        user.setRoles(roles);
        return appUserRepository.save(user);
    }

    @Override
    public List<AppUser> listUsers(UUID churchId) {
        Church church = churchRepository.findById(churchId)
                .orElseThrow(() -> new NotFoundException("Church not found with id: " + churchId));
        AppUser actor = requireAuthenticatedUser();
        validateActorCanManageChurch(actor, church);
        return appUserRepository.findDistinctByChurchId(churchId);
    }

    @Override
    public AppUser updateUser(UUID userId, boolean enabled, Set<UUID> roleIds) {
        AppUser actor = requireAuthenticatedUser();
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        UUID churchId = user.getRoles().stream()
                .map(AppRole::getChurch)
                .filter(church -> church != null && church.getId() != null)
                .map(Church::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User is not linked to a church"));
        Church church = churchRepository.findById(churchId)
                .orElseThrow(() -> new NotFoundException("Church not found with id: " + churchId));
        validateActorCanManageChurch(actor, church);

        Set<AppRole> roles = new LinkedHashSet<>();
        if (roleIds != null && !roleIds.isEmpty()) {
            roleIds.forEach(roleId -> roles.add(resolveRoleById(church, roleId)));
            validateActorCanAssignRoles(actor, church, roles);
            user.setRoles(roles);
        }
        user.setEnabled(enabled);
        return appUserRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        AppUser actor = requireAuthenticatedUser();
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        UUID churchId = user.getRoles().stream()
                .map(AppRole::getChurch)
                .filter(church -> church != null && church.getId() != null)
                .map(Church::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User is not linked to a church"));
        Church church = churchRepository.findById(churchId)
                .orElseThrow(() -> new NotFoundException("Church not found with id: " + churchId));
        validateActorCanManageChurch(actor, church);
        appUserRepository.delete(user);
    }

    private Person resolvePerson(CreateAppUserRequest request) {
        if (request.getPersonId() != null) {
            return personRepository.findById(request.getPersonId())
                    .orElseThrow(() -> new NotFoundException("Person not found with id: " + request.getPersonId()));
        }

        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String fullName = request.getFullName();

        if (fullName == null || fullName.isBlank()) {
            if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
                throw new IllegalArgumentException("Either fullName or firstName + lastName is required");
            }
            fullName = firstName + " " + lastName;
        }

        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            String[] parts = fullName.trim().split("\\s+", 2);
            firstName = parts[0];
            lastName = parts.length > 1 ? parts[1] : parts[0];
        }

        Person person = new Person();
        person.setFullName(fullName);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setPreferredName(request.getFirstName());
        person.setImageUrl(request.getImageUrl());
        person.setEmailAddress(request.getEmailAddress());
        person.setPhoneNumber(request.getPhoneNumber());
        person.setStatus(request.getStatus() != null ? request.getStatus() : Person.PersonStatus.ACTIVE);
        return personRepository.save(person);
    }

    private Set<AppRole> resolveRoles(AppUser actor, Church church, CreateAppUserRequest request) {
        Set<AppRole> roles = new LinkedHashSet<>();

        if (request.getRoleIds() != null) {
            request.getRoleIds().forEach(roleId -> roles.add(resolveRoleById(church, roleId)));
        }

        if (request.getRoleNames() != null) {
            request.getRoleNames().forEach(roleName -> roles.add(resolveRoleByName(church, roleName)));
        }

        if (roles.isEmpty()) {
            roles.add(resolveRoleByName(church, "CHURCH_ADMIN"));
        }

        validateActorCanAssignRoles(actor, church, roles);
        return roles;
    }

    private AppRole resolveRoleById(Church church, UUID roleId) {
        AppRole role = appRoleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleId));
        validateRoleChurch(church, role);
        return role;
    }

    private AppRole resolveRoleByName(Church church, String roleName) {
        AppRole role = appRoleRepository.findByChurchIdAndNameIgnoreCase(church.getId(), roleName)
                .orElseThrow(() -> new NotFoundException("Role not found with name: " + roleName));
        validateRoleChurch(church, role);
        return role;
    }

    private void validateRoleChurch(Church church, AppRole role) {
        if (role.getChurch() == null || !church.getId().equals(role.getChurch().getId())) {
            throw new IllegalArgumentException("Role does not belong to the selected church");
        }
    }

    private AppUser requireAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Authenticated user not found"));
    }

    private void validateActorCanManageChurch(AppUser actor, Church church) {
        if (hasBackofficeAccess(actor)) {
            return;
        }

        if (!actorChurchsById(actor).containsKey(church.getId())) {
            throw new AccessDeniedException("You can only create users for your own church");
        }
    }

    private void validateActorCanAssignRoles(AppUser actor, Church church, Set<AppRole> roles) {
        if (hasBackofficeAccess(actor)) {
            return;
        }

        if (roles.stream().anyMatch(role -> !"CHURCH".equalsIgnoreCase(role.getIdentifier()))) {
            throw new AccessDeniedException("Church-scoped operators cannot assign backoffice roles");
        }

        if (roles.stream().anyMatch(role -> role.getChurch() == null || !church.getId().equals(role.getChurch().getId()))) {
            throw new AccessDeniedException("Church-scoped operators can only assign roles from their own church");
        }
    }

    private boolean hasBackofficeAccess(AppUser actor) {
        return actor.getRoles().stream().anyMatch(role ->
                "ADMIN".equalsIgnoreCase(role.getName()) || "BACKOFFICE".equalsIgnoreCase(role.getIdentifier()));
    }

    private Map<UUID, Church> actorChurchsById(AppUser actor) {
        Map<UUID, Church> churches = new LinkedHashMap<>();
        actor.getRoles().stream()
                .map(AppRole::getChurch)
                .filter(church -> church != null && church.getId() != null)
                .forEach(church -> churches.put(church.getId(), church));
        return churches;
    }
}
