package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.Church;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChurchRepository extends JpaRepository<Church, UUID> {
    Optional<Church> findByNameIgnoreCase(String name);
}
