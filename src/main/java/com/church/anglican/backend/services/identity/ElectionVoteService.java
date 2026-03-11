package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.entities.identity.ElectionVote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ElectionVoteService {
    ElectionVote castVote(UUID electionId, UUID voterPersonId, UUID candidateId);
    Page<ElectionVote> listVotes(UUID electionId, Pageable pageable);
}
