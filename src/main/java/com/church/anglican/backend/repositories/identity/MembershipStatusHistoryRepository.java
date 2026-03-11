package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.MembershipStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MembershipStatusHistoryRepository extends JpaRepository<MembershipStatusHistory, UUID> {
    Page<MembershipStatusHistory> findByMembershipId(UUID membershipId, Pageable pageable);
}
