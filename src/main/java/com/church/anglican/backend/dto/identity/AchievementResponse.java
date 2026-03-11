package com.church.anglican.backend.dto.identity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AchievementResponse {
    private UUID id;
    private UUID churchId;
    private UUID personId;
    private UUID groupId;
    private String title;
    private String description;
    private LocalDateTime achievedAt;
    private LocalDateTime createdAt;
}
