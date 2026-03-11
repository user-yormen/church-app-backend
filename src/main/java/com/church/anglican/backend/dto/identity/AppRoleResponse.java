package com.church.anglican.backend.dto.identity;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AppRoleResponse {
    private UUID id;
    private UUID churchId;
    private String name;
    private String description;
    private String identifier;
    private UUID parentRoleId;
    private List<String> permissions;
}
