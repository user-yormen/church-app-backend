package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.entities.identity.Group;
import com.church.anglican.backend.repositories.identity.GroupRepository;
import com.church.anglican.backend.repositories.identity.spec.GroupSpecifications;
import com.church.anglican.backend.exception.NotFoundException;
import com.church.anglican.backend.services.identity.GroupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    public GroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public Group create(Group group) {
        return groupRepository.save(group);
    }

    @Override
    public Group findById(UUID id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Group not found with id: " + id));
    }

    @Override
    public Page<Group> search(UUID churchId, String query, Group.GroupStatus status, Group.GroupType type, Pageable pageable) {
        Specification<Group> spec = Specification.where(GroupSpecifications.churchIdEquals(churchId));
        if (status != null) {
            spec = spec.and(GroupSpecifications.statusEquals(status));
        }
        if (type != null) {
            spec = spec.and(GroupSpecifications.typeEquals(type));
        }
        if (query != null && !query.isBlank()) {
            spec = spec.and(GroupSpecifications.matchesQuery(query));
        }
        return groupRepository.findAll(spec, pageable);
    }

    @Override
    public Group update(UUID id, Group input) {
        Group group = findById(id);
        group.setName(input.getName());
        group.setDescription(input.getDescription());
        group.setType(input.getType());
        group.setStatus(input.getStatus());
        return groupRepository.save(group);
    }

    @Override
    public void delete(UUID id) {
        groupRepository.delete(findById(id));
    }
}
