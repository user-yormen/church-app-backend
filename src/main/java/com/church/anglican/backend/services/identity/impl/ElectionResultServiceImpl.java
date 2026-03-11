package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.dto.identity.ElectionResultSummaryResponse;
import com.church.anglican.backend.entities.identity.ElectionCandidate;
import com.church.anglican.backend.entities.identity.ElectionVote;
import com.church.anglican.backend.entities.identity.ElectionResult;
import com.church.anglican.backend.repositories.identity.ElectionCandidateRepository;
import com.church.anglican.backend.repositories.identity.ElectionResultRepository;
import com.church.anglican.backend.repositories.identity.ElectionVoteRepository;
import com.church.anglican.backend.services.identity.ElectionResultService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ElectionResultServiceImpl implements ElectionResultService {

    private final ElectionCandidateRepository candidateRepository;
    private final ElectionVoteRepository voteRepository;
    private final ElectionResultRepository resultRepository;

    public ElectionResultServiceImpl(
            ElectionCandidateRepository candidateRepository,
            ElectionVoteRepository voteRepository,
            ElectionResultRepository resultRepository
    ) {
        this.candidateRepository = candidateRepository;
        this.voteRepository = voteRepository;
        this.resultRepository = resultRepository;
    }

    @Override
    public List<ElectionResultSummaryResponse> summarize(UUID electionId) {
        List<ElectionCandidate> candidates = candidateRepository.findByElectionId(electionId, Pageable.unpaged()).getContent();
        List<ElectionVote> votes = voteRepository.findByElectionId(electionId, Pageable.unpaged()).getContent();

        HashMap<UUID, Integer> counts = new HashMap<>();
        for (ElectionVote vote : votes) {
            UUID candidateId = vote.getCandidate().getId();
            counts.put(candidateId, counts.getOrDefault(candidateId, 0) + 1);
        }

        return candidates.stream().map(candidate -> {
            ElectionResultSummaryResponse response = new ElectionResultSummaryResponse();
            response.setCandidateId(candidate.getId());
            response.setPersonId(candidate.getPerson() != null ? candidate.getPerson().getId() : null);
            response.setTotalVotes(counts.getOrDefault(candidate.getId(), 0));
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ElectionResult> persistResults(UUID electionId, UUID winnerPersonId) {
        List<ElectionCandidate> candidates = candidateRepository.findByElectionId(electionId, Pageable.unpaged()).getContent();
        List<ElectionVote> votes = voteRepository.findByElectionId(electionId, Pageable.unpaged()).getContent();

        HashMap<UUID, Integer> counts = new HashMap<>();
        for (ElectionVote vote : votes) {
            UUID candidateId = vote.getCandidate().getId();
            counts.put(candidateId, counts.getOrDefault(candidateId, 0) + 1);
        }

        resultRepository.deleteByElectionId(electionId);

        List<ElectionResult> results = candidates.stream().map(candidate -> {
            ElectionResult result = new ElectionResult();
            result.setElection(candidate.getElection());
            result.setPerson(candidate.getPerson());
            result.setTotalVotes(counts.getOrDefault(candidate.getId(), 0));
            if (winnerPersonId != null && candidate.getPerson() != null && winnerPersonId.equals(candidate.getPerson().getId())) {
                result.setStatus(ElectionResult.ResultStatus.WINNER);
            } else {
                result.setStatus(ElectionResult.ResultStatus.RUNNER_UP);
            }
            return result;
        }).collect(Collectors.toList());

        return resultRepository.saveAll(results);
    }
}
