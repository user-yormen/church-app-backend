package com.church.anglican.backend.dto.identity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateChurchRequest {

    @NotBlank(message = "Church name is required")
    private String name;

    private String address;
}
