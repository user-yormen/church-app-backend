package com.church.anglican.backend.controllers;

import com.church.anglican.backend.dto.financial.CollectionEventResponse;
import com.church.anglican.backend.dto.financial.CreateCollectionEventRequest;
import com.church.anglican.backend.entities.financial.CollectionEvent;
import com.church.anglican.backend.services.financial.CollectionEventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/collections")
public class CollectionEventController {

    private final CollectionEventService collectionEventService;

    public CollectionEventController(CollectionEventService collectionEventService) {
        this.collectionEventService = collectionEventService;
    }

    @PostMapping
    public ResponseEntity<CollectionEventResponse> create(@Valid @RequestBody CreateCollectionEventRequest request) {
        CollectionEvent event = collectionEventService.create(request);
        return ResponseEntity.ok(toResponse(event));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionEventResponse> getById(@PathVariable UUID id) {
        CollectionEvent event = collectionEventService.findById(id);
        return ResponseEntity.ok(toResponse(event));
    }

    @GetMapping
    public ResponseEntity<Page<CollectionEventResponse>> list(
            @RequestParam UUID churchId,
            @RequestParam(required = false) CollectionEvent.CollectionEventStatus status,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<CollectionEvent> events = collectionEventService.list(churchId, status, pageable);
        return ResponseEntity.ok(mapPage(events));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CollectionEventResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateCollectionEventRequest request
    ) {
        return ResponseEntity.ok(toResponse(collectionEventService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        collectionEventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Page<CollectionEventResponse> mapPage(Page<CollectionEvent> page) {
        return new PageImpl<>(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getPageable(),
                page.getTotalElements()
        );
    }

    private CollectionEventResponse toResponse(CollectionEvent event) {
        CollectionEventResponse response = new CollectionEventResponse();
        response.setId(event.getId());
        response.setChurchId(event.getChurch() != null ? event.getChurch().getId() : null);
        response.setCollectionTypeId(event.getCollectionType() != null ? event.getCollectionType().getId() : null);
        response.setServiceReference(event.getServiceReference());
        response.setEventDate(event.getEventDate());
        response.setLocation(event.getLocation());
        response.setCurrency(event.getCurrency());
        response.setStatus(event.getStatus());
        response.setCreatedAt(event.getCreatedAt());
        response.setUpdatedAt(event.getUpdatedAt());
        return response;
    }
}
