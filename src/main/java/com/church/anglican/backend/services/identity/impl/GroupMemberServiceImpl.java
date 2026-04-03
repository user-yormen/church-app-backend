package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.entities.identity.GroupMember;
import com.church.anglican.backend.exception.NotFoundException;
import com.church.anglican.backend.repositories.identity.GroupMemberRepository;
import com.church.anglican.backend.repositories.identity.spec.GroupMemberSpecifications;
import com.church.anglican.backend.services.identity.GroupMemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GroupMemberServiceImpl implements GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;

    public GroupMemberServiceImpl(GroupMemberRepository groupMemberRepository) {
        this.groupMemberRepository = groupMemberRepository;
    }

    @Override
    public GroupMember add(GroupMember member) {
        return groupMemberRepository.save(member);
    }

    @Override
    public Page<GroupMember> list(UUID groupId, UUID personId, GroupMember.MemberStatus status, GroupMember.DuesStatus duesStatus, Pageable pageable) {
        Specification<GroupMember> spec = Specification.where(GroupMemberSpecifications.groupIdEquals(groupId));
        if (personId != null) {
            spec = spec.and(GroupMemberSpecifications.personIdEquals(personId));
        }
        if (status != null) {
            spec = spec.and(GroupMemberSpecifications.statusEquals(status));
        }
        if (duesStatus != null) {
            spec = spec.and(GroupMemberSpecifications.duesStatusEquals(duesStatus));
        }
        return groupMemberRepository.findAll(spec, pageable);
    }

    @Override
    public GroupMember update(UUID groupId, UUID memberId, GroupMember input) {
        GroupMember member = requireMember(groupId, memberId);
        member.setStatus(input.getStatus());
        member.setDuesStatus(input.getDuesStatus());
        member.setJoinedAt(input.getJoinedAt());
        member.setLeftAt(input.getLeftAt());
        member.setDuesPaidThrough(input.getDuesPaidThrough());
        return groupMemberRepository.save(member);
    }

    @Override
    public void delete(UUID groupId, UUID memberId) {
        groupMemberRepository.delete(requireMember(groupId, memberId));
    }

    private GroupMember requireMember(UUID groupId, UUID memberId) {
        GroupMember member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Group member not found with id: " + memberId));
        if (member.getGroup() == null || !groupId.equals(member.getGroup().getId())) {
            throw new NotFoundException("Group member not found for group id: " + groupId);
        }
        return member;
    }
}
