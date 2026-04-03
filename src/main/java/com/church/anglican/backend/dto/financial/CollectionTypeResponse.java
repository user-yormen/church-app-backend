package com.church.anglican.backend.dto.financial;

import lombok.Data;

import java.util.UUID;

@Data
public class CollectionTypeResponse {
    private UUID id;
    private UUID churchId;
    private String name;
    private String description;
    private String defaultFrequency;
    private String expectedCountingMethod;
    private String accountingCategory;
}
