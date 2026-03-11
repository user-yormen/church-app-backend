package com.church.anglican.backend.dto.financial;

import com.church.anglican.backend.entities.financial.CollectionEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateCollectionEventRequest {

    @NotNull(message = "Church ID is required")
    private UUID churchId;

    @NotNull(message = "Collection type ID is required")
    private UUID collectionTypeId;

    private String serviceReference;

    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;

    private String location;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    private String currency;

    @NotNull(message = "Status is required")
    private CollectionEvent.CollectionEventStatus status;
}
