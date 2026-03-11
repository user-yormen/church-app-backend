package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.Person;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreatePersonRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String preferredName;

    private String imageUrl;

    private Person.Gender gender;

    private LocalDateTime dateOfBirth;

    private String phoneNumber;

    @Email(message = "Email should be valid")
    private String emailAddress;

    private String address;

    private String emergencyContact;

    private Person.MaritalStatus maritalStatus;

    @NotNull(message = "Status is required")
    private Person.PersonStatus status;
}
