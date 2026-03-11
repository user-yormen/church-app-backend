package com.church.anglican.backend.services.auth;

import com.church.anglican.backend.dto.auth.BootstrapAdminRequest;
import com.church.anglican.backend.entities.identity.AppRole;
import com.church.anglican.backend.entities.identity.AppUser;
import com.church.anglican.backend.entities.identity.Church;
import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.repositories.identity.AppRoleRepository;
import com.church.anglican.backend.repositories.identity.AppUserRepository;
import com.church.anglican.backend.repositories.identity.ChurchRepository;
import com.church.anglican.backend.repositories.identity.PersonRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class BootstrapAdminService {

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final PersonRepository personRepository;
    private final ChurchRepository churchRepository;
    private final PasswordEncoder passwordEncoder;

    public BootstrapAdminService(
            AppUserRepository appUserRepository,
            AppRoleRepository appRoleRepository,
            PersonRepository personRepository,
            ChurchRepository churchRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.appUserRepository = appUserRepository;
        this.appRoleRepository = appRoleRepository;
        this.personRepository = personRepository;
        this.churchRepository = churchRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser bootstrapAdmin(BootstrapAdminRequest request) {
        appUserRepository.findByUsername(request.getUsername()).ifPresent(existing -> {
            throw new RuntimeException("Username already exists");
        });

        Church church = churchRepository.findById(request.getChurchId())
                .orElseThrow(() -> new RuntimeException("Church not found with id: " + request.getChurchId()));

        AppRole adminRole = appRoleRepository.findByChurchIdAndNameIgnoreCase(church.getId(), "ADMIN")
                .orElseGet(() -> {
                    AppRole role = new AppRole();
                    role.setChurch(church);
                    role.setName("ADMIN");
                    role.setDescription("System administrator");
                    role.setIdentifier("BACKOFFICE");
                    return appRoleRepository.save(role);
                });

        Person person = resolvePerson(request);

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setPerson(person);
        user.setRoles(Collections.singleton(adminRole));

        return appUserRepository.save(user);
    }

    private Person resolvePerson(BootstrapAdminRequest request) {
        if (request.getPersonId() != null) {
            return personRepository.findById(request.getPersonId())
                    .orElseThrow(() -> new RuntimeException("Person not found with id: " + request.getPersonId()));
        }

        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String fullName = request.getFullName();

        if (fullName == null || fullName.isBlank()) {
            if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
                throw new RuntimeException("Either fullName or firstName + lastName is required");
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
        person.setPreferredName(null);
        person.setImageUrl(request.getImageUrl());
        person.setEmailAddress(request.getEmailAddress());
        person.setPhoneNumber(request.getPhoneNumber());
        person.setStatus(request.getStatus() != null ? request.getStatus() : Person.PersonStatus.ACTIVE);

        return personRepository.save(person);
    }
}
