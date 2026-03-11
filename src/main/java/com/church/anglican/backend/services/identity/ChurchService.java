package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.dto.identity.CreateChurchRequest;
import com.church.anglican.backend.entities.identity.Church;

import java.util.UUID;

public interface ChurchService {
    Church createChurch(CreateChurchRequest createChurchRequest);
    Church findById(UUID id);
}
