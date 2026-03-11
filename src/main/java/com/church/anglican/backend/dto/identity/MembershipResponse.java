package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.ChurchMembership;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MembershipResponse {
    private UUID id;
    private UUID personId;
    private UUID churchId;
    private LocalDateTime joinDate;
    private LocalDateTime leaveDate;
    private ChurchMembership.MembershipStatus status;
    private ChurchMembership.MembershipType type;
    private String joinMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
