package com.church.anglican.backend.repositories.configuration;

import com.church.anglican.backend.entities.configuration.CollectionTypeResponsibilityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CollectionTypeResponsibilityRuleRepository extends JpaRepository<CollectionTypeResponsibilityRule, UUID> {
}
