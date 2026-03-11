package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.dto.identity.CreatePersonRequest;
import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.exception.NotFoundException;
import com.church.anglican.backend.repositories.identity.PersonRepository;
import com.church.anglican.backend.services.identity.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Person createPerson(CreatePersonRequest createPersonRequest) {
        Person person = new Person();
        person.setFullName(createPersonRequest.getFullName());
        person.setFirstName(createPersonRequest.getFirstName());
        person.setLastName(createPersonRequest.getLastName());
        person.setPreferredName(createPersonRequest.getPreferredName());
        person.setImageUrl(createPersonRequest.getImageUrl());
        person.setGender(createPersonRequest.getGender());
        person.setDateOfBirth(createPersonRequest.getDateOfBirth());
        person.setPhoneNumber(createPersonRequest.getPhoneNumber());
        person.setEmailAddress(createPersonRequest.getEmailAddress());
        person.setAddress(createPersonRequest.getAddress());
        person.setEmergencyContact(createPersonRequest.getEmergencyContact());
        person.setMaritalStatus(createPersonRequest.getMaritalStatus());
        person.setStatus(createPersonRequest.getStatus());

        return personRepository.save(person);
    }

    @Override
    public Person findById(UUID id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Person not found with id: " + id));
    }
}
