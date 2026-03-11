package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.ShareGrant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateShareGrantRequest {

    @NotBlank(message = "Resource type is required")
    private String resourceType;

    @NotNull(message = "Resource ID is required")
    private UUID resourceId;

    @NotNull(message = "Owner church ID is required")
    private UUID ownerChurchId;

    @NotNull(message = "Grantee church ID is required")
    private UUID granteeChurchId;

    @NotNull(message = "Access level is required")
    private ShareGrant.AccessLevel accessLevel;

    @NotNull(message = "Created by person ID is required")
    private UUID createdByPersonId;
}
