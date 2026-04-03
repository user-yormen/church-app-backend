package com.church.anglican.backend.controllers;

import com.church.anglican.backend.dto.financial.CollectionTypeResponse;
import com.church.anglican.backend.repositories.configuration.CollectionTypeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/collection-types")
public class CollectionTypeController {

    private final CollectionTypeRepository collectionTypeRepository;

    public CollectionTypeController(CollectionTypeRepository collectionTypeRepository) {
        this.collectionTypeRepository = collectionTypeRepository;
    }

    @GetMapping
    public ResponseEntity<List<CollectionTypeResponse>> list(@RequestParam UUID churchId) {
        return ResponseEntity.ok(collectionTypeRepository.findAll().stream()
                .filter(type -> type.getChurch() != null && churchId.equals(type.getChurch().getId()))
                .map(type -> {
                    CollectionTypeResponse response = new CollectionTypeResponse();
                    response.setId(type.getId());
                    response.setChurchId(type.getChurch() != null ? type.getChurch().getId() : null);
                    response.setName(type.getName());
                    response.setDescription(type.getDescription());
                    response.setDefaultFrequency(type.getDefaultFrequency());
                    response.setExpectedCountingMethod(type.getExpectedCountingMethod());
                    response.setAccountingCategory(type.getAccountingCategory());
                    return response;
                })
                .toList());
    }
}
