package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.dto.identity.CreateMembershipRequest;
import com.church.anglican.backend.dto.identity.UpdateMembershipStatusRequest;
import com.church.anglican.backend.entities.identity.Church;
import com.church.anglican.backend.entities.identity.ChurchMembership;
import com.church.anglican.backend.entities.identity.MembershipStatusHistory;
import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.repositories.identity.ChurchMembershipRepository;
import com.church.anglican.backend.repositories.identity.MembershipStatusHistoryRepository;
import com.church.anglican.backend.repositories.identity.PersonRepository;
import com.church.anglican.backend.services.identity.MembershipService;
import com.church.anglican.backend.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MembershipServiceImpl implements MembershipService {

    private final ChurchMembershipRepository churchMembershipRepository;
    private final MembershipStatusHistoryRepository historyRepository;
    private final PersonRepository personRepository;

    public MembershipServiceImpl(
            ChurchMembershipRepository churchMembershipRepository,
            MembershipStatusHistoryRepository historyRepository,
            PersonRepository personRepository
    ) {
        this.churchMembershipRepository = churchMembershipRepository;
        this.historyRepository = historyRepository;
        this.personRepository = personRepository;
    }

    @Override
    public ChurchMembership create(CreateMembershipRequest request) {
        ChurchMembership membership = new ChurchMembership();
        Person person = new Person();
        person.setId(request.getPersonId());
        Church church = new Church();
        church.setId(request.getChurchId());
        membership.setPerson(person);
        membership.setChurch(church);
        membership.setJoinDate(request.getJoinDate());
        membership.setType(request.getType());
        membership.setStatus(request.getStatus());
        membership.setJoinMethod(request.getJoinMethod());
        ChurchMembership saved = churchMembershipRepository.save(membership);
        recordHistory(saved, request.getStatus(), "INITIAL", null, request.getJoinDate());
        return saved;
    }

    @Override
    public ChurchMembership updateStatus(UUID membershipId, UpdateMembershipStatusRequest request) {
        ChurchMembership membership = churchMembershipRepository.findById(membershipId)
                .orElseThrow(() -> new NotFoundException("Membership not found with id: " + membershipId));
        membership.setStatus(request.getStatus());
        ChurchMembership saved = churchMembershipRepository.save(membership);
        recordHistory(saved, request.getStatus(), request.getReason(), request.getChangedByPersonId(), request.getEffectiveDate());
        return saved;
    }

    @Override
    public Page<MembershipStatusHistory> history(UUID membershipId, Pageable pageable) {
        return historyRepository.findByMembershipId(membershipId, pageable);
    }

    private void recordHistory(ChurchMembership membership, ChurchMembership.MembershipStatus status, String reason, UUID changedByPersonId, LocalDateTime effectiveDate) {
        MembershipStatusHistory history = new MembershipStatusHistory();
        history.setMembership(membership);
        history.setStatus(status);
        history.setReason(reason);
        if (changedByPersonId != null) {
            Person changedBy = personRepository.findById(changedByPersonId)
                    .orElseThrow(() -> new NotFoundException("Person not found with id: " + changedByPersonId));
            history.setChangedBy(changedBy);
        }
        history.setEffectiveDate(effectiveDate != null ? effectiveDate : LocalDateTime.now());
        historyRepository.save(history);
    }
}
