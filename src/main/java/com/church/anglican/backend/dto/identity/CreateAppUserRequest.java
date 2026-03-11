package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.Person;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CreateAppUserRequest {

    @NotNull(message = "Church ID is required")
    private UUID churchId;

    private UUID personId;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private String fullName;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private String emailAddress;
    private String phoneNumber;
    private Person.PersonStatus status;

    private Set<UUID> roleIds;
    private Set<String> roleNames;
}
