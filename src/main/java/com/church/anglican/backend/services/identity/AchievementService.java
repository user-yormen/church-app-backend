package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.entities.identity.Achievement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AchievementService {
    Achievement create(Achievement achievement);
    Page<Achievement> list(UUID churchId, UUID personId, UUID groupId, Pageable pageable);
}
