package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByPersonId(UUID personId);
}
