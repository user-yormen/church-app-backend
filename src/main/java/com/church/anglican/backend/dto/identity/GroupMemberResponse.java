package com.church.anglican.backend.dto.identity;

import com.church.anglican.backend.entities.identity.GroupMember;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class GroupMemberResponse {
    private UUID id;
    private UUID groupId;
    private UUID personId;
    private GroupMember.MemberStatus status;
    private GroupMember.DuesStatus duesStatus;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private LocalDateTime duesPaidThrough;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
