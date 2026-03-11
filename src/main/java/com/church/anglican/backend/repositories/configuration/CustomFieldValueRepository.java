package com.church.anglican.backend.repositories.configuration;

import com.church.anglican.backend.entities.configuration.CustomFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomFieldValueRepository extends JpaRepository<CustomFieldValue, UUID> {
}
