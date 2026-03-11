package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.entities.identity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GroupService {
    Group create(Group group);
    Group findById(UUID id);
    Page<Group> search(UUID churchId, String query, Group.GroupStatus status, Group.GroupType type, Pageable pageable);
}
