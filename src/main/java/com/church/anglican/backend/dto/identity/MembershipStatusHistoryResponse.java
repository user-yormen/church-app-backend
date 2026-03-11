package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.ChurchMembership;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MembershipStatusHistoryResponse {
    private UUID id;
    private UUID membershipId;
    private ChurchMembership.MembershipStatus status;
    private String reason;
    private UUID changedByPersonId;
    private LocalDateTime effectiveDate;
    private LocalDateTime createdAt;
}
