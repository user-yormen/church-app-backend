package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.entities.identity.ShareGrant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ShareGrantService {
    ShareGrant create(ShareGrant grant);
    Page<ShareGrant> list(UUID ownerChurchId, UUID granteeChurchId, String resourceType, UUID resourceId, Pageable pageable);
}
