package com.church.anglican.backend.dto.auth;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AuthUserResponse {
    private UUID userId;
    private UUID personId;
    private String username;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private List<String> roles;
    private List<String> roleIdentifiers;
    private List<String> permissions;
}
