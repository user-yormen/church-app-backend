package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.dto.identity.ElectionEligibilityResponse;
import com.church.anglican.backend.entities.identity.*;
import com.church.anglican.backend.repositories.identity.*;
import com.church.anglican.backend.services.identity.impl.ElectionEligibilityServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ElectionEligibilityServiceImplTest {

    @Test
    void evaluate_marks_ineligible_when_dues_not_paid() {
        ElectionRepository electionRepository = Mockito.mock(ElectionRepository.class);
        ElectionEligibilityRuleRepository ruleRepository = Mockito.mock(ElectionEligibilityRuleRepository.class);
        GroupMemberRepository groupMemberRepository = Mockito.mock(GroupMemberRepository.class);
        ChurchMembershipRepository churchMembershipRepository = Mockito.mock(ChurchMembershipRepository.class);
        PersonRepository personRepository = Mockito.mock(PersonRepository.class);
        PersonRoleRepository personRoleRepository = Mockito.mock(PersonRoleRepository.class);

        ElectionEligibilityServiceImpl service = new ElectionEligibilityServiceImpl(
                electionRepository,
                ruleRepository,
                groupMemberRepository,
                churchMembershipRepository,
                personRepository,
                personRoleRepository
        );

        UUID electionId = UUID.randomUUID();
        UUID personId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        UUID churchId = UUID.randomUUID();

        Church church = new Church();
        church.setId(churchId);
        Election election = new Election();
        election.setId(electionId);
        election.setChurch(church);
        election.setScopeType(Election.ElectionScope.GROUP);
        election.setScopeId(groupId);

        Person person = new Person();
        person.setId(personId);
        person.setMaritalStatus(Person.MaritalStatus.SINGLE);
        person.setDateOfBirth(LocalDateTime.now().minusYears(30));

        ElectionEligibilityRule rule = new ElectionEligibilityRule();
        rule.setRuleType(ElectionEligibilityRule.RuleType.DUES_PAID);

        when(electionRepository.findById(electionId)).thenReturn(Optional.of(election));
        when(personRepository.findById(personId)).thenReturn(Optional.of(person));
        when(ruleRepository.findByElectionId(Mockito.eq(electionId), Mockito.eq(org.springframework.data.domain.Pageable.unpaged())))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(rule)));
        when(groupMemberRepository.findByGroupIdAndPersonId(groupId, personId)).thenReturn(Optional.empty());

        ElectionEligibilityResponse response = service.evaluate(electionId, personId);

        assertThat(response.isEligible()).isFalse();
        assertThat(response.getFailedRules()).contains("DUES_PAID");
    }
}
