package com.church.anglican.backend.controllers;

import com.church.anglican.backend.dto.identity.CreateChurchRequest;
import com.church.anglican.backend.entities.identity.Church;
import com.church.anglican.backend.services.identity.ChurchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/churches")
public class ChurchController {

    private final ChurchService churchService;

    @Autowired
    public ChurchController(ChurchService churchService) {
        this.churchService = churchService;
    }

    @PostMapping
    public ResponseEntity<Church> createChurch(@Valid @RequestBody CreateChurchRequest createChurchRequest) {
        Church createdChurch = churchService.createChurch(createChurchRequest);
        return new ResponseEntity<>(createdChurch, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Church> getChurchById(@PathVariable UUID id) {
        try {
            Church church = churchService.findById(id);
            return ResponseEntity.ok(church);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
