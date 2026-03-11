package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.ChurchMembership;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateMembershipRequest {

    @NotNull(message = "Person ID is required")
    private UUID personId;

    @NotNull(message = "Church ID is required")
    private UUID churchId;

    @NotNull(message = "Join date is required")
    private LocalDateTime joinDate;

    @NotNull(message = "Membership type is required")
    private ChurchMembership.MembershipType type;

    @NotNull(message = "Membership status is required")
    private ChurchMembership.MembershipStatus status;

    private String joinMethod;
}
