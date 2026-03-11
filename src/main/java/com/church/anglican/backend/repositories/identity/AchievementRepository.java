package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
    Page<Achievement> findByChurchId(UUID churchId, Pageable pageable);

    Page<Achievement> findByChurchIdAndPersonId(UUID churchId, UUID personId, Pageable pageable);

    Page<Achievement> findByChurchIdAndGroupId(UUID churchId, UUID groupId, Pageable pageable);
}
