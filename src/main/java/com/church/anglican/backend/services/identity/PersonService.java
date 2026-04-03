package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.dto.identity.CreatePersonRequest;
import com.church.anglican.backend.entities.identity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PersonService {
    Person createPerson(CreatePersonRequest createPersonRequest);
    Person findById(UUID id);
    Page<Person> search(String query, Person.PersonStatus status, Pageable pageable);
    Person updatePerson(UUID id, CreatePersonRequest request);
    void deletePerson(UUID id);
}
