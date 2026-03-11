package com.church.anglican.backend.controllers;

import com.church.anglican.backend.entities.identity.*;
import com.church.anglican.backend.repositories.identity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ElectionControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private ChurchRepository churchRepository;
    @Autowired private GroupRepository groupRepository;
    @Autowired private PersonRepository personRepository;
    @Autowired private GroupMemberRepository groupMemberRepository;
    @Autowired private AppRoleRepository appRoleRepository;
    @Autowired private ElectionRepository electionRepository;
    @Autowired private ElectionEligibilityRuleRepository ruleRepository;
    @Autowired private ElectionCandidateRepository candidateRepository;

    @Test
    @WithMockUser(authorities = "ADMIN")
    void castVote_enforces_eligibility() throws Exception {
        Church church = new Church();
        church.setName("Test Church");
        church.setAddress("Address");
        church = churchRepository.save(church);

        Group group = new Group();
        group.setChurch(church);
        group.setName("AYPA");
        group.setType(Group.GroupType.GUILD);
        group.setStatus(Group.GroupStatus.ACTIVE);
        group = groupRepository.save(group);

        AppRole role = new AppRole();
        role.setChurch(church);
        role.setName("PRESIDENT");
        role = appRoleRepository.save(role);

        Person voter = new Person();
        voter.setFullName("Voter One");
        voter.setFirstName("Voter");
        voter.setLastName("One");
        voter.setStatus(Person.PersonStatus.ACTIVE);
        voter = personRepository.save(voter);

        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setPerson(voter);
        member.setStatus(GroupMember.MemberStatus.ACTIVE);
        member.setDuesStatus(GroupMember.DuesStatus.PAID);
        member.setJoinedAt(LocalDateTime.now().minusDays(30));
        groupMemberRepository.save(member);

        Person candidatePerson = new Person();
        candidatePerson.setFullName("Candidate One");
        candidatePerson.setFirstName("Candidate");
        candidatePerson.setLastName("One");
        candidatePerson.setStatus(Person.PersonStatus.ACTIVE);
        candidatePerson = personRepository.save(candidatePerson);

        Election election = new Election();
        election.setChurch(church);
        election.setScopeType(Election.ElectionScope.GROUP);
        election.setScopeId(group.getId());
        election.setRoleToAssign(role);
        election.setTitle("AYPA Election");
        election.setNominationStart(LocalDateTime.now().minusDays(2));
        election.setNominationEnd(LocalDateTime.now().minusDays(1));
        election.setVotingStart(LocalDateTime.now().minusHours(2));
        election.setVotingEnd(LocalDateTime.now().plusHours(2));
        election.setStatus(Election.ElectionStatus.VOTING);
        election = electionRepository.save(election);

        ElectionEligibilityRule rule = new ElectionEligibilityRule();
        rule.setElection(election);
        rule.setRuleType(ElectionEligibilityRule.RuleType.DUES_PAID);
        ruleRepository.save(rule);

        ElectionCandidate candidate = new ElectionCandidate();
        candidate.setElection(election);
        candidate.setPerson(candidatePerson);
        candidate.setStatus(ElectionCandidate.CandidateStatus.APPROVED);
        candidate = candidateRepository.save(candidate);

        var payload = new java.util.HashMap<String, Object>();
        payload.put("voterPersonId", voter.getId().toString());
        payload.put("candidateId", candidate.getId().toString());

        mockMvc.perform(post("/api/v1/elections/" + election.getId() + "/votes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        member.setDuesStatus(GroupMember.DuesStatus.OVERDUE);
        groupMemberRepository.save(member);

        mockMvc.perform(post("/api/v1/elections/" + election.getId() + "/votes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }
}
