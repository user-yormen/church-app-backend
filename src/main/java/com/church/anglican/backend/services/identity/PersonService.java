package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.dto.identity.CreatePersonRequest;
import com.church.anglican.backend.entities.identity.Person;

import java.util.UUID;

public interface PersonService {
    Person createPerson(CreatePersonRequest createPersonRequest);
    Person findById(UUID id);
}
