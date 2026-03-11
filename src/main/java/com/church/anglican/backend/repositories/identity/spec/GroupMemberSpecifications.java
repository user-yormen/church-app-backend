package com.church.anglican.backend.repositories.identity.spec;

import com.church.anglican.backend.entities.identity.GroupMember;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class GroupMemberSpecifications {

    private GroupMemberSpecifications() {}

    public static Specification<GroupMember> groupIdEquals(UUID groupId) {
        return (root, query, cb) -> cb.equal(root.get("group").get("id"), groupId);
    }

    public static Specification<GroupMember> personIdEquals(UUID personId) {
        return (root, query, cb) -> cb.equal(root.get("person").get("id"), personId);
    }

    public static Specification<GroupMember> statusEquals(GroupMember.MemberStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<GroupMember> duesStatusEquals(GroupMember.DuesStatus duesStatus) {
        return (root, query, cb) -> cb.equal(root.get("duesStatus"), duesStatus);
    }
}
