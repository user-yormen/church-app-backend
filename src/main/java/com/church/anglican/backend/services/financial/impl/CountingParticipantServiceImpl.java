package com.church.anglican.backend.services.financial.impl;

import com.church.anglican.backend.dto.financial.AddCountingParticipantRequest;
import com.church.anglican.backend.entities.financial.CollectionEvent;
import com.church.anglican.backend.entities.financial.CountingParticipant;
import com.church.anglican.backend.entities.financial.CountingSession;
import com.church.anglican.backend.entities.identity.AppRole;
import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.entities.identity.PersonRole;
import com.church.anglican.backend.repositories.financial.CollectionEventRepository;
import com.church.anglican.backend.repositories.financial.CountingParticipantRepository;
import com.church.anglican.backend.repositories.financial.CountingSessionRepository;
import com.church.anglican.backend.repositories.identity.PersonRoleRepository;
import com.church.anglican.backend.services.financial.CountingParticipantService;
import com.church.anglican.backend.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CountingParticipantServiceImpl implements CountingParticipantService {

    private final CountingParticipantRepository participantRepository;
    private final CountingSessionRepository sessionRepository;
    private final CollectionEventRepository eventRepository;
    private final PersonRoleRepository personRoleRepository;

    public CountingParticipantServiceImpl(
            CountingParticipantRepository participantRepository,
            CountingSessionRepository sessionRepository,
            CollectionEventRepository eventRepository,
            PersonRoleRepository personRoleRepository
    ) {
        this.participantRepository = participantRepository;
        this.sessionRepository = sessionRepository;
        this.eventRepository = eventRepository;
        this.personRoleRepository = personRoleRepository;
    }

    @Override
    public CountingParticipant add(UUID countingSessionId, AddCountingParticipantRequest request) {
        CountingSession session = sessionRepository.findById(countingSessionId)
                .orElseThrow(() -> new NotFoundException("Counting session not found with id: " + countingSessionId));
        CollectionEvent event = eventRepository.findById(session.getCollectionEvent().getId())
                .orElseThrow(() -> new NotFoundException("Collection event not found with id: " + session.getCollectionEvent().getId()));

        boolean hasRole = personRoleRepository.existsByPersonIdAndRoleIdAndScopeTypeAndScopeIdAndStatus(
                request.getPersonId(),
                request.getRoleId(),
                PersonRole.RoleScope.CHURCH,
                event.getChurch().getId(),
                PersonRole.AssignmentStatus.ACTIVE
        );
        if (!hasRole) {
            throw new RuntimeException("Person does not have required active role for this church");
        }

        CountingParticipant participant = new CountingParticipant();
        participant.setCountingSession(session);
        Person person = new Person();
        person.setId(request.getPersonId());
        participant.setPerson(person);
        AppRole role = new AppRole();
        role.setId(request.getRoleId());
        participant.setRoleAtTimeOfCounting(role);
        participant.setParticipationType(request.getParticipationType());
        return participantRepository.save(participant);
    }

    @Override
    public Page<CountingParticipant> list(UUID countingSessionId, Pageable pageable) {
        return participantRepository.findByCountingSessionId(countingSessionId, pageable);
    }
}
