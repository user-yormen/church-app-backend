package com.church.anglican.backend.controllers;

import com.church.anglican.backend.dto.identity.CreatePersonRequest;
import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.services.identity.PersonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/people")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@Valid @RequestBody CreatePersonRequest createPersonRequest) {
        Person createdPerson = personService.createPerson(createPersonRequest);
        return new ResponseEntity<>(createdPerson, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable UUID id) {
        try {
            Person person = personService.findById(id);
            return ResponseEntity.ok(person);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
