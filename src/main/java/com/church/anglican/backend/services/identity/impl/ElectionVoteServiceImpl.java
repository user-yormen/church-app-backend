package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.dto.identity.ElectionEligibilityResponse;
import com.church.anglican.backend.entities.identity.Election;
import com.church.anglican.backend.entities.identity.ElectionCandidate;
import com.church.anglican.backend.entities.identity.ElectionVote;
import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.repositories.identity.ElectionCandidateRepository;
import com.church.anglican.backend.repositories.identity.ElectionRepository;
import com.church.anglican.backend.repositories.identity.ElectionVoteRepository;
import com.church.anglican.backend.repositories.identity.PersonRepository;
import com.church.anglican.backend.services.identity.ElectionEligibilityService;
import com.church.anglican.backend.services.identity.ElectionVoteService;
import com.church.anglican.backend.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ElectionVoteServiceImpl implements ElectionVoteService {

    private final ElectionRepository electionRepository;
    private final ElectionCandidateRepository candidateRepository;
    private final ElectionVoteRepository voteRepository;
    private final PersonRepository personRepository;
    private final ElectionEligibilityService eligibilityService;

    public ElectionVoteServiceImpl(
            ElectionRepository electionRepository,
            ElectionCandidateRepository candidateRepository,
            ElectionVoteRepository voteRepository,
            PersonRepository personRepository,
            ElectionEligibilityService eligibilityService
    ) {
        this.electionRepository = electionRepository;
        this.candidateRepository = candidateRepository;
        this.voteRepository = voteRepository;
        this.personRepository = personRepository;
        this.eligibilityService = eligibilityService;
    }

    @Override
    public ElectionVote castVote(UUID electionId, UUID voterPersonId, UUID candidateId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new NotFoundException("Election not found with id: " + electionId));
        if (election.getStatus() != Election.ElectionStatus.VOTING) {
            throw new RuntimeException("Election is not open for voting");
        }
        if (voteRepository.existsByElectionIdAndVoterId(electionId, voterPersonId)) {
            throw new RuntimeException("Voter has already voted");
        }
        ElectionCandidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new NotFoundException("Candidate not found with id: " + candidateId));
        if (!candidate.getElection().getId().equals(electionId)) {
            throw new RuntimeException("Candidate does not belong to this election");
        }
        if (candidate.getStatus() != ElectionCandidate.CandidateStatus.APPROVED
                && candidate.getStatus() != ElectionCandidate.CandidateStatus.NOMINATED) {
            throw new RuntimeException("Candidate is not eligible for voting");
        }

        ElectionEligibilityResponse eligibility = eligibilityService.evaluate(electionId, voterPersonId);
        if (!eligibility.isEligible()) {
            throw new RuntimeException("Voter is not eligible: " + String.join(", ", eligibility.getFailedRules()));
        }

        Person voter = personRepository.findById(voterPersonId)
                .orElseThrow(() -> new NotFoundException("Person not found with id: " + voterPersonId));
        ElectionVote vote = new ElectionVote();
        vote.setElection(election);
        vote.setCandidate(candidate);
        vote.setVoter(voter);
        return voteRepository.save(vote);
    }

    @Override
    public Page<ElectionVote> listVotes(UUID electionId, Pageable pageable) {
        return voteRepository.findByElectionId(electionId, pageable);
    }
}
