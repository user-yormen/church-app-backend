package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.entities.identity.GroupMember;
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
}
