package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.GroupMember;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AddGroupMemberRequest {

    @NotNull(message = "Person ID is required")
    private UUID personId;

    @NotNull(message = "Member status is required")
    private GroupMember.MemberStatus status;

    @NotNull(message = "Dues status is required")
    private GroupMember.DuesStatus duesStatus;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    private LocalDateTime duesPaidThrough;
}
