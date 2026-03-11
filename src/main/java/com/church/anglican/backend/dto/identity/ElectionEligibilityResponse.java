package com.church.anglican.backend.dto.identity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class ElectionEligibilityResponse {
    private UUID electionId;
    private UUID personId;
    private boolean eligible;
    private List<String> failedRules = new ArrayList<>();
}
