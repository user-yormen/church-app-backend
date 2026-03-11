package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.entities.identity.Achievement;
import com.church.anglican.backend.repositories.identity.AchievementRepository;
import com.church.anglican.backend.services.identity.AchievementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;

    public AchievementServiceImpl(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    @Override
    public Achievement create(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    @Override
    public Page<Achievement> list(UUID churchId, UUID personId, UUID groupId, Pageable pageable) {
        if (personId != null) {
            return achievementRepository.findByChurchIdAndPersonId(churchId, personId, pageable);
        }
        if (groupId != null) {
            return achievementRepository.findByChurchIdAndGroupId(churchId, groupId, pageable);
        }
        return achievementRepository.findByChurchId(churchId, pageable);
    }
}
