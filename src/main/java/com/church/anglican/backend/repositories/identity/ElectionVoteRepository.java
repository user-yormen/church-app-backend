package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.ElectionVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ElectionVoteRepository extends JpaRepository<ElectionVote, UUID> {
    Page<ElectionVote> findByElectionId(UUID electionId, Pageable pageable);

    boolean existsByElectionIdAndVoterId(UUID electionId, UUID voterPersonId);
}
