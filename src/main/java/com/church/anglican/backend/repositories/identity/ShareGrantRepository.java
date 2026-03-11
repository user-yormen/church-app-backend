package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.ShareGrant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ShareGrantRepository extends JpaRepository<ShareGrant, UUID> {
    Page<ShareGrant> findByOwnerChurchId(UUID ownerChurchId, Pageable pageable);

    Page<ShareGrant> findByGranteeChurchId(UUID granteeChurchId, Pageable pageable);

    Page<ShareGrant> findByOwnerChurchIdAndResourceType(UUID ownerChurchId, String resourceType, Pageable pageable);

    Page<ShareGrant> findByResourceTypeAndResourceId(String resourceType, UUID resourceId, Pageable pageable);
}
