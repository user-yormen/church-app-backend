package com.church.anglican.backend.services.identity.impl;

import com.church.anglican.backend.dto.identity.CreatePersonRequest;
import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.exception.NotFoundException;
import com.church.anglican.backend.repositories.identity.PersonRepository;
import com.church.anglican.backend.services.identity.PersonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Page<Person> search(String query, Person.PersonStatus status, Pageable pageable) {
        return personRepository.search(query, status, pageable);
    }

    @Override
    public Person updatePerson(UUID id, CreatePersonRequest request) {
        Person person = findById(id);
        person.setFullName(request.getFullName());
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setPreferredName(request.getPreferredName());
        person.setImageUrl(request.getImageUrl());
        person.setGender(request.getGender());
        person.setDateOfBirth(request.getDateOfBirth());
        person.setPhoneNumber(request.getPhoneNumber());
        person.setEmailAddress(request.getEmailAddress());
        person.setAddress(request.getAddress());
        person.setEmergencyContact(request.getEmergencyContact());
        person.setMaritalStatus(request.getMaritalStatus());
        person.setStatus(request.getStatus());
        return personRepository.save(person);
    }

    @Override
    public void deletePerson(UUID id) {
        personRepository.delete(findById(id));
    }
}
