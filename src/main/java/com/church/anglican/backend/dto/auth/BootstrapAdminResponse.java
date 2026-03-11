package com.church.anglican.backend.dto.auth;

import lombok.Data;

import java.util.UUID;

@Data
public class BootstrapAdminResponse {
    private UUID userId;
    private UUID personId;
    private String username;
}
