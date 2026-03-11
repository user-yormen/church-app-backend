package com.church.anglican.backend.dto.identity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateAchievementRequest {

    @NotNull(message = "Church ID is required")
    private UUID churchId;

    private UUID personId;

    private UUID groupId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private LocalDateTime achievedAt;
}
