package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppRoleRepository extends JpaRepository<AppRole, UUID> {
    Optional<AppRole> findByChurchIdAndNameIgnoreCase(UUID churchId, String name);
    List<AppRole> findAllByChurchIdOrderByNameAsc(UUID churchId);
}
