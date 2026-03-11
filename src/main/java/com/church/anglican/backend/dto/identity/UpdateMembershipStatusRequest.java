package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.ChurchMembership;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UpdateMembershipStatusRequest {

    @NotNull(message = "Status is required")
    private ChurchMembership.MembershipStatus status;

    private LocalDateTime effectiveDate;

    private String reason;

    private UUID changedByPersonId;
}
