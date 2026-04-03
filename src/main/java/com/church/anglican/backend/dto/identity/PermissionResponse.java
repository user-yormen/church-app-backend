package com.church.anglican.backend.dto.identity;

import lombok.Data;

import java.util.UUID;

@Data
public class PermissionResponse {
    private UUID id;
    private String name;
    private String description;
    private String identifier;
}
