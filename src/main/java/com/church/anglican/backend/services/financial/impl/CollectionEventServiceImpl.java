package com.church.anglican.backend.services.financial.impl;

import com.church.anglican.backend.dto.financial.CreateCollectionEventRequest;
import com.church.anglican.backend.entities.configuration.CollectionType;
import com.church.anglican.backend.entities.financial.CollectionEvent;
import com.church.anglican.backend.entities.identity.Church;
import com.church.anglican.backend.repositories.financial.CollectionEventRepository;
import com.church.anglican.backend.services.financial.CollectionEventService;
import com.church.anglican.backend.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CollectionEventServiceImpl implements CollectionEventService {

    private final CollectionEventRepository collectionEventRepository;

    public CollectionEventServiceImpl(CollectionEventRepository collectionEventRepository) {
        this.collectionEventRepository = collectionEventRepository;
    }

    @Override
    public CollectionEvent create(CreateCollectionEventRequest request) {
        CollectionEvent event = new CollectionEvent();
        Church church = new Church();
        church.setId(request.getChurchId());
        CollectionType type = new CollectionType();
        type.setId(request.getCollectionTypeId());
        event.setChurch(church);
        event.setCollectionType(type);
        event.setServiceReference(request.getServiceReference());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setCurrency(request.getCurrency());
        event.setStatus(request.getStatus());
        return collectionEventRepository.save(event);
    }

    @Override
    public CollectionEvent findById(UUID id) {
        return collectionEventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Collection event not found with id: " + id));
    }

    @Override
    public Page<CollectionEvent> list(UUID churchId, CollectionEvent.CollectionEventStatus status, Pageable pageable) {
        if (status != null) {
            return collectionEventRepository.findByChurchIdAndStatus(churchId, status, pageable);
        }
        return collectionEventRepository.findByChurchId(churchId, pageable);
    }

    @Override
    public CollectionEvent update(UUID id, CreateCollectionEventRequest request) {
        CollectionEvent event = findById(id);
        CollectionType type = new CollectionType();
        type.setId(request.getCollectionTypeId());
        event.setCollectionType(type);
        event.setServiceReference(request.getServiceReference());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setCurrency(request.getCurrency());
        event.setStatus(request.getStatus());
        return collectionEventRepository.save(event);
    }

    @Override
    public void delete(UUID id) {
        collectionEventRepository.delete(findById(id));
    }
}
