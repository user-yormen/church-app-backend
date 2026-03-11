package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.dto.identity.CreateChurchRequest;
import com.church.anglican.backend.entities.identity.Church;
import com.church.anglican.backend.exception.NotFoundException;
import com.church.anglican.backend.repositories.identity.ChurchRepository;
import com.church.anglican.backend.services.identity.AccessProvisioningService;
import com.church.anglican.backend.services.identity.ChurchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChurchServiceImpl implements ChurchService {

    private final ChurchRepository churchRepository;
    private final AccessProvisioningService accessProvisioningService;

    @Autowired
    public ChurchServiceImpl(ChurchRepository churchRepository, AccessProvisioningService accessProvisioningService) {
        this.churchRepository = churchRepository;
        this.accessProvisioningService = accessProvisioningService;
    }

    @Override
    public Church createChurch(CreateChurchRequest createChurchRequest) {
        Church church = new Church();
        church.setName(createChurchRequest.getName());
        church.setAddress(createChurchRequest.getAddress());
        Church savedChurch = churchRepository.save(church);
        accessProvisioningService.provisionForChurch(savedChurch);
        return savedChurch;
    }

    @Override
    public Church findById(UUID id) {
        return churchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Church not found with id: " + id));
    }
}
