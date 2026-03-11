package com.church.anglican.backend.dto.identity;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AppUserResponse {
    private UUID userId;
    private UUID personId;
    private UUID churchId;
    private String username;
    private List<String> roles;
    private List<String> roleIdentifiers;
}
