package com.church.anglican.backend.dto.financial;

import com.church.anglican.backend.entities.financial.CollectionEvent;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CollectionEventResponse {
    private UUID id;
    private UUID churchId;
    private UUID collectionTypeId;
    private String serviceReference;
    private LocalDateTime eventDate;
    private String location;
    private String currency;
    private CollectionEvent.CollectionEventStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
