package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.ChurchMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChurchMembershipRepository extends JpaRepository<ChurchMembership, UUID> {
    Optional<ChurchMembership> findByChurchIdAndPersonId(UUID churchId, UUID personId);
}
