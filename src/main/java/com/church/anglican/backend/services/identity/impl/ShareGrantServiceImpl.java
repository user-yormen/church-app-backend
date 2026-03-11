package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.entities.identity.ShareGrant;
import com.church.anglican.backend.repositories.identity.ShareGrantRepository;
import com.church.anglican.backend.services.identity.ShareGrantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShareGrantServiceImpl implements ShareGrantService {

    private final ShareGrantRepository shareGrantRepository;

    public ShareGrantServiceImpl(ShareGrantRepository shareGrantRepository) {
        this.shareGrantRepository = shareGrantRepository;
    }

    @Override
    public ShareGrant create(ShareGrant grant) {
        return shareGrantRepository.save(grant);
    }

    @Override
    public Page<ShareGrant> list(UUID ownerChurchId, UUID granteeChurchId, String resourceType, UUID resourceId, Pageable pageable) {
        if (resourceType != null && resourceId != null) {
            return shareGrantRepository.findByResourceTypeAndResourceId(resourceType, resourceId, pageable);
        }
        if (ownerChurchId != null && resourceType != null) {
            return shareGrantRepository.findByOwnerChurchIdAndResourceType(ownerChurchId, resourceType, pageable);
        }
        if (ownerChurchId != null) {
            return shareGrantRepository.findByOwnerChurchId(ownerChurchId, pageable);
        }
        if (granteeChurchId != null) {
            return shareGrantRepository.findByGranteeChurchId(granteeChurchId, pageable);
        }
        return Page.empty(pageable);
    }
}
