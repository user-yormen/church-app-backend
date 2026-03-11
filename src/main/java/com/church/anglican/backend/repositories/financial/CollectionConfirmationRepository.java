package com.church.anglican.backend.repositories.financial;

import com.church.anglican.backend.entities.financial.CollectionConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CollectionConfirmationRepository extends JpaRepository<CollectionConfirmation, UUID> {
}
