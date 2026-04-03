package com.church.anglican.backend.dto.identity;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AppUserResponse {
    private UUID userId;
    private UUID personId;
    private UUID churchId;
    private String personName;
    private String emailAddress;
    private String phoneNumber;
    private String username;
    private boolean enabled;
    private List<String> roles;
    private List<String> roleIdentifiers;
}
