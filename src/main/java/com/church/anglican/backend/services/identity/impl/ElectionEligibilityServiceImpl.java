package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.dto.identity.ElectionEligibilityResponse;
import com.church.anglican.backend.entities.identity.*;
import com.church.anglican.backend.repositories.identity.*;
import com.church.anglican.backend.services.identity.ElectionEligibilityService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.church.anglican.backend.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Map;
import java.util.UUID;

@Service
public class ElectionEligibilityServiceImpl implements ElectionEligibilityService {

    private final ElectionRepository electionRepository;
    private final ElectionEligibilityRuleRepository ruleRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ChurchMembershipRepository churchMembershipRepository;
    private final PersonRepository personRepository;
    private final PersonRoleRepository personRoleRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ElectionEligibilityServiceImpl(
            ElectionRepository electionRepository,
            ElectionEligibilityRuleRepository ruleRepository,
            GroupMemberRepository groupMemberRepository,
            ChurchMembershipRepository churchMembershipRepository,
            PersonRepository personRepository,
            PersonRoleRepository personRoleRepository
    ) {
        this.electionRepository = electionRepository;
        this.ruleRepository = ruleRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.churchMembershipRepository = churchMembershipRepository;
        this.personRepository = personRepository;
        this.personRoleRepository = personRoleRepository;
    }

    @Override
    public ElectionEligibilityResponse evaluate(UUID electionId, UUID personId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new NotFoundException("Election not found with id: " + electionId));
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new NotFoundException("Person not found with id: " + personId));

        ElectionEligibilityResponse response = new ElectionEligibilityResponse();
        response.setElectionId(electionId);
        response.setPersonId(personId);

        for (ElectionEligibilityRule rule : ruleRepository.findByElectionId(electionId, org.springframework.data.domain.Pageable.unpaged()).getContent()) {
            if (!passesRule(rule, election, person)) {
                response.getFailedRules().add(rule.getRuleType().name());
            }
        }

        response.setEligible(response.getFailedRules().isEmpty());
        return response;
    }

    private boolean passesRule(ElectionEligibilityRule rule, Election election, Person person) {
        Map<String, Object> config = parseConfig(rule.getRuleConfig());
        switch (rule.getRuleType()) {
            case DUES_PAID:
                return checkDuesPaid(election, person.getId());
            case MARRIED:
                return person.getMaritalStatus() == Person.MaritalStatus.MARRIED;
            case MEMBER_DURATION_DAYS:
                return checkMemberDuration(election, person.getId(), getInt(config, "days"));
            case AGE_AT_LEAST:
                return checkAgeAtLeast(person, getInt(config, "years"));
            case HAS_ROLE:
                return checkHasRole(election, person.getId(), getUuid(config, "roleId"));
            default:
                return false;
        }
    }

    private boolean checkDuesPaid(Election election, UUID personId) {
        if (election.getScopeType() != Election.ElectionScope.GROUP) {
            return false;
        }
        return groupMemberRepository.findByGroupIdAndPersonId(election.getScopeId(), personId)
                .map(m -> m.getDuesStatus() == GroupMember.DuesStatus.PAID && m.getStatus() == GroupMember.MemberStatus.ACTIVE)
                .orElse(false);
    }

    private boolean checkMemberDuration(Election election, UUID personId, int days) {
        LocalDateTime now = LocalDateTime.now();
        if (election.getScopeType() == Election.ElectionScope.GROUP) {
            return groupMemberRepository.findByGroupIdAndPersonId(election.getScopeId(), personId)
                    .map(m -> m.getJoinedAt() != null && m.getJoinedAt().plusDays(days).isBefore(now))
                    .orElse(false);
        }
        return churchMembershipRepository.findByChurchIdAndPersonId(election.getChurch().getId(), personId)
                .map(m -> m.getJoinDate().plusDays(days).isBefore(now))
                .orElse(false);
    }

    private boolean checkAgeAtLeast(Person person, int years) {
        if (person.getDateOfBirth() == null) {
            return false;
        }
        int age = Period.between(person.getDateOfBirth().toLocalDate(), LocalDateTime.now().toLocalDate()).getYears();
        return age >= years;
    }

    private boolean checkHasRole(Election election, UUID personId, UUID roleId) {
        if (roleId == null) {
            return false;
        }
        if (election.getScopeType() == Election.ElectionScope.GROUP) {
            return personRoleRepository.existsByPersonIdAndRoleIdAndScopeTypeAndScopeIdAndStatus(
                    personId, roleId, PersonRole.RoleScope.GROUP, election.getScopeId(), PersonRole.AssignmentStatus.ACTIVE
            );
        }
        return personRoleRepository.existsByPersonIdAndRoleIdAndScopeTypeAndScopeIdAndStatus(
                personId, roleId, PersonRole.RoleScope.CHURCH, election.getChurch().getId(), PersonRole.AssignmentStatus.ACTIVE
        );
    }

    private Map<String, Object> parseConfig(String raw) {
        if (raw == null || raw.isBlank()) {
            return java.util.Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(raw, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            return java.util.Collections.emptyMap();
        }
    }

    private int getInt(Map<String, Object> config, String key) {
        Object val = config.get(key);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        if (val instanceof String) {
            return Integer.parseInt((String) val);
        }
        return 0;
    }

    private UUID getUuid(Map<String, Object> config, String key) {
        Object val = config.get(key);
        if (val instanceof String) {
            try {
                return UUID.fromString((String) val);
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }
}
