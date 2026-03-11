package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID>, JpaSpecificationExecutor<GroupMember> {
    Page<GroupMember> findByGroupId(UUID groupId, Pageable pageable);

    Page<GroupMember> findByGroupIdAndStatus(UUID groupId, GroupMember.MemberStatus status, Pageable pageable);

    Page<GroupMember> findByGroupIdAndDuesStatus(UUID groupId, GroupMember.DuesStatus duesStatus, Pageable pageable);

    Optional<GroupMember> findByGroupIdAndPersonId(UUID groupId, UUID personId);
}
