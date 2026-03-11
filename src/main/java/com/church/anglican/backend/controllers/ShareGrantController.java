package com.church.anglican.backend.controllers;

import com.church.anglican.backend.dto.identity.CreateShareGrantRequest;
import com.church.anglican.backend.dto.identity.ShareGrantResponse;
import com.church.anglican.backend.entities.identity.Church;
import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.entities.identity.ShareGrant;
import com.church.anglican.backend.services.identity.ShareGrantService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shares")
public class ShareGrantController {

    private final ShareGrantService shareGrantService;

    public ShareGrantController(ShareGrantService shareGrantService) {
        this.shareGrantService = shareGrantService;
    }

    @PostMapping
    public ResponseEntity<ShareGrantResponse> create(@Valid @RequestBody CreateShareGrantRequest request) {
        ShareGrant grant = new ShareGrant();
        grant.setResourceType(request.getResourceType());
        grant.setResourceId(request.getResourceId());
        Church owner = new Church();
        owner.setId(request.getOwnerChurchId());
        grant.setOwnerChurch(owner);
        Church grantee = new Church();
        grantee.setId(request.getGranteeChurchId());
        grant.setGranteeChurch(grantee);
        Person createdBy = new Person();
        createdBy.setId(request.getCreatedByPersonId());
        grant.setCreatedBy(createdBy);
        grant.setAccessLevel(request.getAccessLevel());
        ShareGrant saved = shareGrantService.create(grant);
        return ResponseEntity.ok(toShareGrantResponse(saved));
    }

    @GetMapping
    public ResponseEntity<Page<ShareGrantResponse>> list(
            @RequestParam(required = false) UUID ownerChurchId,
            @RequestParam(required = false) UUID granteeChurchId,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) UUID resourceId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<ShareGrant> grants = shareGrantService.list(ownerChurchId, granteeChurchId, resourceType, resourceId, pageable);
        return ResponseEntity.ok(mapShareGrantPage(grants));
    }

    private Page<ShareGrantResponse> mapShareGrantPage(Page<ShareGrant> page) {
        return new PageImpl<>(
                page.getContent().stream().map(this::toShareGrantResponse).toList(),
                page.getPageable(),
                page.getTotalElements()
        );
    }

    private ShareGrantResponse toShareGrantResponse(ShareGrant grant) {
        ShareGrantResponse response = new ShareGrantResponse();
        response.setId(grant.getId());
        response.setResourceType(grant.getResourceType());
        response.setResourceId(grant.getResourceId());
        response.setOwnerChurchId(grant.getOwnerChurch() != null ? grant.getOwnerChurch().getId() : null);
        response.setGranteeChurchId(grant.getGranteeChurch() != null ? grant.getGranteeChurch().getId() : null);
        response.setAccessLevel(grant.getAccessLevel());
        response.setCreatedByPersonId(grant.getCreatedBy() != null ? grant.getCreatedBy().getId() : null);
        response.setCreatedAt(grant.getCreatedAt());
        return response;
    }
}
