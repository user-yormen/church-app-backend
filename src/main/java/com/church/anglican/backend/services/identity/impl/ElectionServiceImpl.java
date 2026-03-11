package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.entities.identity.AppRole;
import com.church.anglican.backend.entities.identity.AppUser;
import com.church.anglican.backend.entities.identity.Election;
import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.entities.identity.PersonRole;
import com.church.anglican.backend.repositories.identity.AppUserRepository;
import com.church.anglican.backend.repositories.identity.ElectionRepository;
import com.church.anglican.backend.repositories.identity.PersonRepository;
import com.church.anglican.backend.repositories.identity.PersonRoleRepository;
import com.church.anglican.backend.services.identity.ElectionResultService;
import com.church.anglican.backend.exception.NotFoundException;
import com.church.anglican.backend.repositories.identity.spec.ElectionSpecifications;
import com.church.anglican.backend.services.identity.ElectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ElectionServiceImpl implements ElectionService {

    private final ElectionRepository electionRepository;
    private final PersonRoleRepository personRoleRepository;
    private final AppUserRepository appUserRepository;
    private final PersonRepository personRepository;
    private final ElectionResultService resultService;

    public ElectionServiceImpl(
            ElectionRepository electionRepository,
            PersonRoleRepository personRoleRepository,
            AppUserRepository appUserRepository,
            PersonRepository personRepository,
            ElectionResultService resultService
    ) {
        this.electionRepository = electionRepository;
        this.personRoleRepository = personRoleRepository;
        this.appUserRepository = appUserRepository;
        this.personRepository = personRepository;
        this.resultService = resultService;
    }

    @Override
    public Election create(Election election) {
        validateTimeline(election);
        return electionRepository.save(election);
    }

    @Override
    public Election findById(UUID id) {
        return electionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Election not found with id: " + id));
    }

    @Override
    public Page<Election> list(UUID churchId, Election.ElectionStatus status, Election.ElectionScope scopeType, UUID scopeId, UUID roleId, Pageable pageable) {
        Specification<Election> spec = Specification.where(ElectionSpecifications.churchIdEquals(churchId));
        if (scopeType != null && scopeId != null) {
            spec = spec.and(ElectionSpecifications.scopeEquals(scopeType, scopeId));
        }
        if (status != null) {
            spec = spec.and(ElectionSpecifications.statusEquals(status));
        }
        if (roleId != null) {
            spec = spec.and(ElectionSpecifications.roleIdEquals(roleId));
        }
        return electionRepository.findAll(spec, pageable);
    }

    @Override
    public Election finalizeElection(UUID electionId, UUID winnerPersonId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new NotFoundException("Election not found with id: " + electionId));
        Person winner = personRepository.findById(winnerPersonId)
                .orElseThrow(() -> new NotFoundException("Person not found with id: " + winnerPersonId));

        expireExistingAssignments(election);

        PersonRole assignment = new PersonRole();
        assignment.setPerson(winner);
        assignment.setRole(election.getRoleToAssign());
        assignment.setScopeType(election.getScopeType() == Election.ElectionScope.GROUP ? PersonRole.RoleScope.GROUP : PersonRole.RoleScope.CHURCH);
        assignment.setScopeId(election.getScopeType() == Election.ElectionScope.GROUP ? election.getScopeId() : election.getChurch().getId());
        assignment.setAssignedDate(LocalDateTime.now());
        assignment.setSource(PersonRole.AssignmentSource.ELECTION);
        assignment.setStatus(PersonRole.AssignmentStatus.ACTIVE);
        personRoleRepository.save(assignment);

        syncUserRole(winner.getId(), election.getRoleToAssign());

        resultService.persistResults(election.getId(), winnerPersonId);

        election.setStatus(Election.ElectionStatus.FINALIZED);
        return electionRepository.save(election);
    }

    private void expireExistingAssignments(Election election) {
        UUID roleId = election.getRoleToAssign().getId();
        PersonRole.RoleScope scopeType = election.getScopeType() == Election.ElectionScope.GROUP ? PersonRole.RoleScope.GROUP : PersonRole.RoleScope.CHURCH;
        UUID scopeId = election.getScopeType() == Election.ElectionScope.GROUP ? election.getScopeId() : election.getChurch().getId();

        for (PersonRole existing : personRoleRepository.findByRoleIdAndScopeTypeAndScopeIdAndStatus(roleId, scopeType, scopeId, PersonRole.AssignmentStatus.ACTIVE)) {
            existing.setStatus(PersonRole.AssignmentStatus.EXPIRED);
            existing.setExpiryDate(LocalDateTime.now());
            personRoleRepository.save(existing);
            removeUserRole(existing.getPerson().getId(), election.getRoleToAssign());
        }
    }

    private void syncUserRole(UUID personId, AppRole role) {
        appUserRepository.findByPersonId(personId).ifPresent(user -> {
            if (user.getRoles().stream().noneMatch(r -> r.getId().equals(role.getId()))) {
                user.getRoles().add(role);
                appUserRepository.save(user);
            }
        });
    }

    private void removeUserRole(UUID personId, AppRole role) {
        appUserRepository.findByPersonId(personId).ifPresent(user -> {
            boolean removed = user.getRoles().removeIf(r -> r.getId().equals(role.getId()));
            if (removed) {
                appUserRepository.save(user);
            }
        });
    }

    private void validateTimeline(Election election) {
        if (election.getNominationStart() == null || election.getNominationEnd() == null
                || election.getVotingStart() == null || election.getVotingEnd() == null) {
            throw new RuntimeException("Election timeline is incomplete");
        }
        if (!election.getNominationStart().isBefore(election.getNominationEnd())) {
            throw new RuntimeException("Nomination start must be before nomination end");
        }
        if (!election.getNominationEnd().isBefore(election.getVotingStart())) {
            throw new RuntimeException("Nomination end must be before voting start");
        }
        if (!election.getVotingStart().isBefore(election.getVotingEnd())) {
            throw new RuntimeException("Voting start must be before voting end");
        }
    }
}
