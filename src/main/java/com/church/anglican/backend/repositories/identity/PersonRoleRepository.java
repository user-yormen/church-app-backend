package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.PersonRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonRoleRepository extends JpaRepository<PersonRole, UUID> {
    List<PersonRole> findByRoleIdAndScopeTypeAndScopeIdAndStatus(UUID roleId, PersonRole.RoleScope scopeType, UUID scopeId, PersonRole.AssignmentStatus status);

    boolean existsByPersonIdAndRoleIdAndScopeTypeAndScopeIdAndStatus(UUID personId, UUID roleId, PersonRole.RoleScope scopeType, UUID scopeId, PersonRole.AssignmentStatus status);

    boolean existsByPersonIdAndRoleIdAndStatus(UUID personId, UUID roleId, PersonRole.AssignmentStatus status);
}
