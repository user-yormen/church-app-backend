package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.ElectionEligibilityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ElectionEligibilityRuleRepository extends JpaRepository<ElectionEligibilityRule, UUID> {
    Page<ElectionEligibilityRule> findByElectionId(UUID electionId, Pageable pageable);
}
