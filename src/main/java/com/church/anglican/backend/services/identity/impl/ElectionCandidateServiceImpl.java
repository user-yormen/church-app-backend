package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.entities.identity.Election;
import com.church.anglican.backend.entities.identity.ElectionCandidate;
import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.repositories.identity.ElectionCandidateRepository;
import com.church.anglican.backend.repositories.identity.ElectionRepository;
import com.church.anglican.backend.repositories.identity.PersonRepository;
import com.church.anglican.backend.services.identity.ElectionEligibilityService;
import com.church.anglican.backend.services.identity.ElectionCandidateService;
import com.church.anglican.backend.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ElectionCandidateServiceImpl implements ElectionCandidateService {

    private final ElectionRepository electionRepository;
    private final ElectionCandidateRepository candidateRepository;
    private final PersonRepository personRepository;
    private final ElectionEligibilityService eligibilityService;

    public ElectionCandidateServiceImpl(
            ElectionRepository electionRepository,
            ElectionCandidateRepository candidateRepository,
            PersonRepository personRepository,
            ElectionEligibilityService eligibilityService
    ) {
        this.electionRepository = electionRepository;
        this.candidateRepository = candidateRepository;
        this.personRepository = personRepository;
        this.eligibilityService = eligibilityService;
    }

    @Override
    public ElectionCandidate addCandidate(UUID electionId, UUID personId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new NotFoundException("Election not found with id: " + electionId));
        if (election.getStatus() != Election.ElectionStatus.NOMINATION) {
            throw new RuntimeException("Election is not open for nominations");
        }
        if (candidateRepository.findByElectionIdAndPersonId(electionId, personId).isPresent()) {
            throw new RuntimeException("Candidate already nominated");
        }
        var eligibility = eligibilityService.evaluate(electionId, personId);
        if (!eligibility.isEligible()) {
            throw new RuntimeException("Candidate is not eligible: " + String.join(", ", eligibility.getFailedRules()));
        }
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new NotFoundException("Person not found with id: " + personId));
        ElectionCandidate candidate = new ElectionCandidate();
        candidate.setElection(election);
        candidate.setPerson(person);
        candidate.setStatus(ElectionCandidate.CandidateStatus.NOMINATED);
        return candidateRepository.save(candidate);
    }

    @Override
    public Page<ElectionCandidate> listCandidates(UUID electionId, Pageable pageable) {
        return candidateRepository.findByElectionId(electionId, pageable);
    }
}
