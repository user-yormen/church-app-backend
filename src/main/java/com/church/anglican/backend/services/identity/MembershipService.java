package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.dto.identity.CreateMembershipRequest;
import com.church.anglican.backend.dto.identity.UpdateMembershipStatusRequest;
import com.church.anglican.backend.entities.identity.ChurchMembership;
import com.church.anglican.backend.entities.identity.MembershipStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MembershipService {
    ChurchMembership create(CreateMembershipRequest request);
    ChurchMembership updateStatus(UUID membershipId, UpdateMembershipStatusRequest request);
    Page<MembershipStatusHistory> history(UUID membershipId, Pageable pageable);
    Page<ChurchMembership> list(UUID churchId, String query, ChurchMembership.MembershipStatus status, ChurchMembership.MembershipType type, Pageable pageable);
    ChurchMembership findById(UUID membershipId);
    void delete(UUID membershipId);
}
