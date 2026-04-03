package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.entities.identity.GroupMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GroupMemberService {
    GroupMember add(GroupMember member);
    Page<GroupMember> list(UUID groupId, UUID personId, GroupMember.MemberStatus status, GroupMember.DuesStatus duesStatus, Pageable pageable);
    GroupMember update(UUID groupId, UUID memberId, GroupMember member);
    void delete(UUID groupId, UUID memberId);
}
