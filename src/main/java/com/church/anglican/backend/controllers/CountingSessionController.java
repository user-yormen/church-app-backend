package com.church.anglican.backend.controllers;

import com.church.anglican.backend.dto.financial.*;
import com.church.anglican.backend.entities.financial.CollectionAmountBreakdown;
import com.church.anglican.backend.entities.financial.CountingParticipant;
import com.church.anglican.backend.entities.financial.CountingSession;
import com.church.anglican.backend.services.financial.CollectionAmountBreakdownService;
import com.church.anglican.backend.services.financial.CountingParticipantService;
import com.church.anglican.backend.services.financial.CountingSessionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/counting-sessions")
public class CountingSessionController {

    private final CountingSessionService countingSessionService;
    private final CountingParticipantService countingParticipantService;
    private final CollectionAmountBreakdownService breakdownService;

    public CountingSessionController(
            CountingSessionService countingSessionService,
            CountingParticipantService countingParticipantService,
            CollectionAmountBreakdownService breakdownService
    ) {
        this.countingSessionService = countingSessionService;
        this.countingParticipantService = countingParticipantService;
        this.breakdownService = breakdownService;
    }

    @PostMapping
    public ResponseEntity<CountingSessionResponse> create(@Valid @RequestBody CreateCountingSessionRequest request) {
        CountingSession session = countingSessionService.create(request);
        return ResponseEntity.ok(toResponse(session));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountingSessionResponse> getById(@PathVariable UUID id) {
        CountingSession session = countingSessionService.findById(id);
        return ResponseEntity.ok(toResponse(session));
    }

    @GetMapping
    public ResponseEntity<Page<CountingSessionResponse>> list(
            @RequestParam UUID collectionEventId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<CountingSession> sessions = countingSessionService.list(collectionEventId, pageable);
        return ResponseEntity.ok(mapSessionPage(sessions));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CountingSessionResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateCountingSessionRequest request
    ) {
        return ResponseEntity.ok(toResponse(countingSessionService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        countingSessionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/participants")
    public ResponseEntity<CountingParticipantResponse> addParticipant(
            @PathVariable UUID id,
            @Valid @RequestBody AddCountingParticipantRequest request
    ) {
        CountingParticipant participant = countingParticipantService.add(id, request);
        return ResponseEntity.ok(toResponse(participant));
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<Page<CountingParticipantResponse>> listParticipants(
            @PathVariable UUID id,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<CountingParticipant> participants = countingParticipantService.list(id, pageable);
        return ResponseEntity.ok(mapParticipantPage(participants));
    }

    @PostMapping("/{id}/breakdowns")
    public ResponseEntity<CollectionAmountBreakdownResponse> addBreakdown(
            @PathVariable UUID id,
            @Valid @RequestBody CreateCollectionAmountBreakdownRequest request
    ) {
        request.setCountingSessionId(id);
        CollectionAmountBreakdown breakdown = breakdownService.create(request);
        return ResponseEntity.ok(toResponse(breakdown));
    }

    @GetMapping("/{id}/breakdowns")
    public ResponseEntity<Page<CollectionAmountBreakdownResponse>> listBreakdowns(
            @PathVariable UUID id,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<CollectionAmountBreakdown> breakdowns = breakdownService.list(id, pageable);
        return ResponseEntity.ok(mapBreakdownPage(breakdowns));
    }

    private Page<CountingSessionResponse> mapSessionPage(Page<CountingSession> page) {
        return new PageImpl<>(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getPageable(),
                page.getTotalElements()
        );
    }

    private CountingSessionResponse toResponse(CountingSession session) {
        CountingSessionResponse response = new CountingSessionResponse();
        response.setId(session.getId());
        response.setCollectionEventId(session.getCollectionEvent() != null ? session.getCollectionEvent().getId() : null);
        response.setCountingMethod(session.getCountingMethod());
        response.setStartTime(session.getStartTime());
        response.setEndTime(session.getEndTime());
        response.setStatus(session.getStatus());
        response.setCreatedAt(session.getCreatedAt());
        response.setUpdatedAt(session.getUpdatedAt());
        return response;
    }

    private Page<CountingParticipantResponse> mapParticipantPage(Page<CountingParticipant> page) {
        return new PageImpl<>(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getPageable(),
                page.getTotalElements()
        );
    }

    private CountingParticipantResponse toResponse(CountingParticipant participant) {
        CountingParticipantResponse response = new CountingParticipantResponse();
        response.setId(participant.getId());
        response.setCountingSessionId(participant.getCountingSession() != null ? participant.getCountingSession().getId() : null);
        response.setPersonId(participant.getPerson() != null ? participant.getPerson().getId() : null);
        response.setRoleId(participant.getRoleAtTimeOfCounting() != null ? participant.getRoleAtTimeOfCounting().getId() : null);
        response.setParticipationType(participant.getParticipationType());
        response.setCreatedAt(participant.getCreatedAt());
        response.setUpdatedAt(participant.getUpdatedAt());
        return response;
    }

    private Page<CollectionAmountBreakdownResponse> mapBreakdownPage(Page<CollectionAmountBreakdown> page) {
        return new PageImpl<>(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getPageable(),
                page.getTotalElements()
        );
    }

    private CollectionAmountBreakdownResponse toResponse(CollectionAmountBreakdown breakdown) {
        CollectionAmountBreakdownResponse response = new CollectionAmountBreakdownResponse();
        response.setId(breakdown.getId());
        response.setCountingSessionId(breakdown.getCountingSession() != null ? breakdown.getCountingSession().getId() : null);
        response.setTotalAmount(breakdown.getTotalAmount());
        response.setNotesAmount(breakdown.getNotesAmount());
        response.setCoinsAmount(breakdown.getCoinsAmount());
        response.setChequesAmount(breakdown.getChequesAmount());
        response.setTransfersAmount(breakdown.getTransfersAmount());
        response.setDenominationBreakdown(breakdown.getDenominationBreakdown());
        response.setCreatedAt(breakdown.getCreatedAt());
        response.setUpdatedAt(breakdown.getUpdatedAt());
        return response;
    }
}
