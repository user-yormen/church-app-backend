package com.church.anglican.backend.controllers;

import com.church.anglican.backend.dto.identity.CreateElectionRequest;
import com.church.anglican.backend.dto.identity.ElectionEligibilityResponse;
import com.church.anglican.backend.dto.identity.CreateElectionCandidateRequest;
import com.church.anglican.backend.dto.identity.CastVoteRequest;
import com.church.anglican.backend.dto.identity.ElectionCandidateResponse;
import com.church.anglican.backend.dto.identity.ElectionVoteResponse;
import com.church.anglican.backend.dto.identity.ElectionResultSummaryResponse;
import com.church.anglican.backend.dto.identity.ElectionResponse;
import com.church.anglican.backend.dto.identity.FinalizeElectionRequest;
import com.church.anglican.backend.entities.identity.AppRole;
import com.church.anglican.backend.entities.identity.Church;
import com.church.anglican.backend.entities.identity.Election;
import com.church.anglican.backend.services.identity.ElectionService;
import com.church.anglican.backend.services.identity.ElectionEligibilityService;
import com.church.anglican.backend.services.identity.ElectionCandidateService;
import com.church.anglican.backend.services.identity.ElectionVoteService;
import com.church.anglican.backend.services.identity.ElectionResultService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/elections")
public class ElectionController {

    private final ElectionService electionService;
    private final ElectionEligibilityService eligibilityService;
    private final ElectionCandidateService candidateService;
    private final ElectionVoteService voteService;
    private final ElectionResultService resultService;

    public ElectionController(
            ElectionService electionService,
            ElectionEligibilityService eligibilityService,
            ElectionCandidateService candidateService,
            ElectionVoteService voteService,
            ElectionResultService resultService
    ) {
        this.electionService = electionService;
        this.eligibilityService = eligibilityService;
        this.candidateService = candidateService;
        this.voteService = voteService;
        this.resultService = resultService;
    }

    @PostMapping
    public ResponseEntity<ElectionResponse> create(@Valid @RequestBody CreateElectionRequest request) {
        Church church = new Church();
        church.setId(request.getChurchId());
        AppRole role = new AppRole();
        role.setId(request.getRoleId());
        Election election = new Election();
        election.setChurch(church);
        election.setScopeType(request.getScopeType());
        election.setScopeId(request.getScopeId());
        election.setRoleToAssign(role);
        election.setTitle(request.getTitle());
        election.setDescription(request.getDescription());
        election.setNominationStart(request.getNominationStart());
        election.setNominationEnd(request.getNominationEnd());
        election.setVotingStart(request.getVotingStart());
        election.setVotingEnd(request.getVotingEnd());
        election.setStatus(request.getStatus());
        Election saved = electionService.create(election);
        return ResponseEntity.ok(toElectionResponse(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ElectionResponse> getById(@PathVariable UUID id) {
        Election election = electionService.findById(id);
        return ResponseEntity.ok(toElectionResponse(election));
    }

    @GetMapping
    public ResponseEntity<Page<ElectionResponse>> list(
            @RequestParam UUID churchId,
            @RequestParam(required = false) Election.ElectionStatus status,
            @RequestParam(required = false) Election.ElectionScope scopeType,
            @RequestParam(required = false) UUID scopeId,
            @RequestParam(required = false) UUID roleId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<Election> elections = electionService.list(churchId, status, scopeType, scopeId, roleId, pageable);
        return ResponseEntity.ok(mapElectionPage(elections));
    }

    @GetMapping("/{id}/eligibility")
    public ResponseEntity<ElectionEligibilityResponse> checkEligibility(
            @PathVariable UUID id,
            @RequestParam UUID personId
    ) {
        return ResponseEntity.ok(eligibilityService.evaluate(id, personId));
    }

    @PostMapping("/{id}/candidates")
    public ResponseEntity<ElectionCandidateResponse> addCandidate(
            @PathVariable UUID id,
            @Valid @RequestBody CreateElectionCandidateRequest request
    ) {
        var candidate = candidateService.addCandidate(id, request.getPersonId());
        return ResponseEntity.ok(toCandidateResponse(candidate));
    }

    @GetMapping("/{id}/candidates")
    public ResponseEntity<Page<ElectionCandidateResponse>> listCandidates(
            @PathVariable UUID id,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        var candidates = candidateService.listCandidates(id, pageable);
        return ResponseEntity.ok(mapCandidatePage(candidates));
    }

    @PostMapping("/{id}/votes")
    public ResponseEntity<ElectionVoteResponse> castVote(
            @PathVariable UUID id,
            @Valid @RequestBody CastVoteRequest request
    ) {
        var vote = voteService.castVote(id, request.getVoterPersonId(), request.getCandidateId());
        return ResponseEntity.ok(toVoteResponse(vote));
    }

    @GetMapping("/{id}/votes")
    public ResponseEntity<Page<ElectionVoteResponse>> listVotes(
            @PathVariable UUID id,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        var votes = voteService.listVotes(id, pageable);
        return ResponseEntity.ok(mapVotePage(votes));
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<java.util.List<ElectionResultSummaryResponse>> results(@PathVariable UUID id) {
        return ResponseEntity.ok(resultService.summarize(id));
    }

    @PostMapping("/{id}/finalize")
    public ResponseEntity<ElectionResponse> finalizeElection(
            @PathVariable UUID id,
            @Valid @RequestBody FinalizeElectionRequest request
    ) {
        Election election = electionService.finalizeElection(id, request.getWinnerPersonId());
        return ResponseEntity.ok(toElectionResponse(election));
    }

    private Page<ElectionResponse> mapElectionPage(Page<Election> page) {
        return new PageImpl<>(
                page.getContent().stream().map(this::toElectionResponse).toList(),
                page.getPageable(),
                page.getTotalElements()
        );
    }

    private ElectionResponse toElectionResponse(Election election) {
        ElectionResponse response = new ElectionResponse();
        response.setId(election.getId());
        response.setChurchId(election.getChurch() != null ? election.getChurch().getId() : null);
        response.setScopeType(election.getScopeType());
        response.setScopeId(election.getScopeId());
        response.setRoleId(election.getRoleToAssign() != null ? election.getRoleToAssign().getId() : null);
        response.setTitle(election.getTitle());
        response.setDescription(election.getDescription());
        response.setNominationStart(election.getNominationStart());
        response.setNominationEnd(election.getNominationEnd());
        response.setVotingStart(election.getVotingStart());
        response.setVotingEnd(election.getVotingEnd());
        response.setStatus(election.getStatus());
        response.setCreatedAt(election.getCreatedAt());
        response.setUpdatedAt(election.getUpdatedAt());
        return response;
    }

    private Page<ElectionCandidateResponse> mapCandidatePage(Page<com.church.anglican.backend.entities.identity.ElectionCandidate> page) {
        return new PageImpl<>(
                page.getContent().stream().map(this::toCandidateResponse).toList(),
                page.getPageable(),
                page.getTotalElements()
        );
    }

    private ElectionCandidateResponse toCandidateResponse(com.church.anglican.backend.entities.identity.ElectionCandidate candidate) {
        ElectionCandidateResponse response = new ElectionCandidateResponse();
        response.setId(candidate.getId());
        response.setElectionId(candidate.getElection() != null ? candidate.getElection().getId() : null);
        response.setPersonId(candidate.getPerson() != null ? candidate.getPerson().getId() : null);
        response.setStatus(candidate.getStatus());
        response.setCreatedAt(candidate.getCreatedAt());
        return response;
    }

    private Page<ElectionVoteResponse> mapVotePage(Page<com.church.anglican.backend.entities.identity.ElectionVote> page) {
        return new PageImpl<>(
                page.getContent().stream().map(this::toVoteResponse).toList(),
                page.getPageable(),
                page.getTotalElements()
        );
    }

    private ElectionVoteResponse toVoteResponse(com.church.anglican.backend.entities.identity.ElectionVote vote) {
        ElectionVoteResponse response = new ElectionVoteResponse();
        response.setId(vote.getId());
        response.setElectionId(vote.getElection() != null ? vote.getElection().getId() : null);
        response.setVoterPersonId(vote.getVoter() != null ? vote.getVoter().getId() : null);
        response.setCandidateId(vote.getCandidate() != null ? vote.getCandidate().getId() : null);
        response.setCreatedAt(vote.getCreatedAt());
        return response;
    }
}
