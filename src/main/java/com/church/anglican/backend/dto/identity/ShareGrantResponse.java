package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.ShareGrant;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ShareGrantResponse {
    private UUID id;
    private String resourceType;
    private UUID resourceId;
    private UUID ownerChurchId;
    private UUID granteeChurchId;
    private ShareGrant.AccessLevel accessLevel;
    private UUID createdByPersonId;
    private LocalDateTime createdAt;
}
