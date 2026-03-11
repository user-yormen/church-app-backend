package com.church.anglican.backend.repositories.identity.spec;

import com.church.anglican.backend.entities.identity.Group;
import org.springframework.data.jpa.domain.Specification;

public final class GroupSpecifications {

    private GroupSpecifications() {}

    public static Specification<Group> churchIdEquals(java.util.UUID churchId) {
        return (root, query, cb) -> cb.equal(root.get("church").get("id"), churchId);
    }

    public static Specification<Group> statusEquals(Group.GroupStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Group> typeEquals(Group.GroupType type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<Group> matchesQuery(String queryText) {
        String q = "%" + queryText.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), q),
                cb.like(cb.lower(root.get("description")), q)
        );
    }
}
