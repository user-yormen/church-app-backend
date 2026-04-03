package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByPersonId(UUID personId);

    @Query("""
        select distinct u from AppUser u
        join fetch u.roles r
        join fetch u.person p
        where r.church.id = :churchId
        order by p.fullName asc
        """)
    List<AppUser> findDistinctByChurchId(@Param("churchId") UUID churchId);
}
