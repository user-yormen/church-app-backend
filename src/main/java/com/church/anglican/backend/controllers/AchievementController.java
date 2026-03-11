package com.church.anglican.backend.controllers;

import com.church.anglican.backend.dto.identity.AchievementResponse;
import com.church.anglican.backend.dto.identity.CreateAchievementRequest;
import com.church.anglican.backend.entities.identity.Church;
import com.church.anglican.backend.entities.identity.Achievement;
import com.church.anglican.backend.entities.identity.Group;
import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.services.identity.AchievementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/achievements")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @PostMapping
    public ResponseEntity<AchievementResponse> create(@Valid @RequestBody CreateAchievementRequest request) {
        Achievement achievement = new Achievement();
        Church church = new Church();
        church.setId(request.getChurchId());
        achievement.setChurch(church);
        if (request.getPersonId() != null) {
            Person person = new Person();
            person.setId(request.getPersonId());
            achievement.setPerson(person);
        }
        if (request.getGroupId() != null) {
            Group group = new Group();
            group.setId(request.getGroupId());
            achievement.setGroup(group);
        }
        achievement.setTitle(request.getTitle());
        achievement.setDescription(request.getDescription());
        achievement.setAchievedAt(request.getAchievedAt());
        Achievement saved = achievementService.create(achievement);
        return ResponseEntity.ok(toAchievementResponse(saved));
    }

    @GetMapping
    public ResponseEntity<Page<AchievementResponse>> list(
            @RequestParam UUID churchId,
            @RequestParam(required = false) UUID personId,
            @RequestParam(required = false) UUID groupId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<Achievement> achievements = achievementService.list(churchId, personId, groupId, pageable);
        return ResponseEntity.ok(mapAchievementPage(achievements));
    }

    private Page<AchievementResponse> mapAchievementPage(Page<Achievement> page) {
        return new PageImpl<>(
                page.getContent().stream().map(this::toAchievementResponse).toList(),
                page.getPageable(),
                page.getTotalElements()
        );
    }

    private AchievementResponse toAchievementResponse(Achievement achievement) {
        AchievementResponse response = new AchievementResponse();
        response.setId(achievement.getId());
        response.setChurchId(achievement.getChurch() != null ? achievement.getChurch().getId() : null);
        response.setPersonId(achievement.getPerson() != null ? achievement.getPerson().getId() : null);
        response.setGroupId(achievement.getGroup() != null ? achievement.getGroup().getId() : null);
        response.setTitle(achievement.getTitle());
        response.setDescription(achievement.getDescription());
        response.setAchievedAt(achievement.getAchievedAt());
        response.setCreatedAt(achievement.getCreatedAt());
        return response;
    }
}
