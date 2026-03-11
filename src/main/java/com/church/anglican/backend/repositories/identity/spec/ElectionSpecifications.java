package com.church.anglican.backend.repositories.identity.spec;

import com.church.anglican.backend.entities.identity.Election;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class ElectionSpecifications {

    private ElectionSpecifications() {}

    public static Specification<Election> churchIdEquals(UUID churchId) {
        return (root, query, cb) -> cb.equal(root.get("church").get("id"), churchId);
    }

    public static Specification<Election> statusEquals(Election.ElectionStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Election> scopeEquals(Election.ElectionScope scopeType, UUID scopeId) {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("scopeType"), scopeType),
                cb.equal(root.get("scopeId"), scopeId)
        );
    }

    public static Specification<Election> roleIdEquals(UUID roleId) {
        return (root, query, cb) -> cb.equal(root.get("roleToAssign").get("id"), roleId);
    }
}
