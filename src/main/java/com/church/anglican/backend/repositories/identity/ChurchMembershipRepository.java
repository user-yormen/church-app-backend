package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.ChurchMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChurchMembershipRepository extends JpaRepository<ChurchMembership, UUID> {
    Optional<ChurchMembership> findByChurchIdAndPersonId(UUID churchId, UUID personId);

    @Query("""
        select m from ChurchMembership m
        join m.person p
        where m.church.id = :churchId
          and (:status is null or m.status = :status)
          and (:type is null or m.type = :type)
          and (:q is null or trim(:q) = '' or
               lower(p.fullName) like lower(concat('%', :q, '%')) or
               lower(coalesce(p.emailAddress, '')) like lower(concat('%', :q, '%')) or
               lower(coalesce(p.phoneNumber, '')) like lower(concat('%', :q, '%')))
        """)
    Page<ChurchMembership> search(
            @Param("churchId") UUID churchId,
            @Param("q") String query,
            @Param("status") ChurchMembership.MembershipStatus status,
            @Param("type") ChurchMembership.MembershipType type,
            Pageable pageable
    );
}
