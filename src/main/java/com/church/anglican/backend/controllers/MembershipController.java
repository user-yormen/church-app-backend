package com.church.anglican.backend.controllers;

import com.church.anglican.backend.dto.identity.CreateMembershipRequest;
import com.church.anglican.backend.dto.identity.MembershipResponse;
import com.church.anglican.backend.dto.identity.MembershipStatusHistoryResponse;
import com.church.anglican.backend.dto.identity.UpdateMembershipStatusRequest;
import com.church.anglican.backend.entities.identity.ChurchMembership;
import com.church.anglican.backend.entities.identity.MembershipStatusHistory;
import com.church.anglican.backend.services.identity.MembershipService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/memberships")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PostMapping
    public ResponseEntity<MembershipResponse> create(@Valid @RequestBody CreateMembershipRequest request) {
        ChurchMembership membership = membershipService.create(request);
        return ResponseEntity.ok(toMembershipResponse(membership));
    }

    @PutMapping("/{membershipId}/status")
    public ResponseEntity<MembershipResponse> updateStatus(
            @PathVariable UUID membershipId,
            @Valid @RequestBody UpdateMembershipStatusRequest request
    ) {
        ChurchMembership membership = membershipService.updateStatus(membershipId, request);
        return ResponseEntity.ok(toMembershipResponse(membership));
    }

    @GetMapping("/{membershipId}/history")
    public ResponseEntity<Page<MembershipStatusHistoryResponse>> history(
            @PathVariable UUID membershipId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<MembershipStatusHistory> history = membershipService.history(membershipId, pageable);
        return ResponseEntity.ok(mapHistoryPage(history));
    }

    private MembershipResponse toMembershipResponse(ChurchMembership membership) {
        MembershipResponse response = new MembershipResponse();
        response.setId(membership.getId());
        response.setPersonId(membership.getPerson() != null ? membership.getPerson().getId() : null);
        response.setChurchId(membership.getChurch() != null ? membership.getChurch().getId() : null);
        response.setJoinDate(membership.getJoinDate());
        response.setLeaveDate(membership.getLeaveDate());
        response.setStatus(membership.getStatus());
        response.setType(membership.getType());
        response.setJoinMethod(membership.getJoinMethod());
        response.setCreatedAt(membership.getCreatedAt());
        response.setUpdatedAt(membership.getUpdatedAt());
        return response;
    }

    private Page<MembershipStatusHistoryResponse> mapHistoryPage(Page<MembershipStatusHistory> page) {
        return new PageImpl<>(
                page.getContent().stream().map(this::toHistoryResponse).toList(),
                page.getPageable(),
                page.getTotalElements()
        );
    }

    private MembershipStatusHistoryResponse toHistoryResponse(MembershipStatusHistory history) {
        MembershipStatusHistoryResponse response = new MembershipStatusHistoryResponse();
        response.setId(history.getId());
        response.setMembershipId(history.getMembership() != null ? history.getMembership().getId() : null);
        response.setStatus(history.getStatus());
        response.setReason(history.getReason());
        response.setChangedByPersonId(history.getChangedBy() != null ? history.getChangedBy().getId() : null);
        response.setEffectiveDate(history.getEffectiveDate());
        response.setCreatedAt(history.getCreatedAt());
        return response;
    }
}
