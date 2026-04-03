package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.GroupMember;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateGroupMemberRequest {

    @NotNull(message = "Member status is required")
    private GroupMember.MemberStatus status;

    @NotNull(message = "Dues status is required")
    private GroupMember.DuesStatus duesStatus;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    private LocalDateTime duesPaidThrough;
}
