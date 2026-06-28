package com.skillstorm.retirementplanner.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.retirementplanner.dtos.ContributionDto;
import com.skillstorm.retirementplanner.models.Contribution;
import com.skillstorm.retirementplanner.services.ContributionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("contributions")
@CrossOrigin({"http://127.0.0.1:5500", "http://localhost:4200"})
public class ContributionsController {

    private final ContributionService service;

    public ContributionsController(ContributionService service) {
        this.service = service;
    }
    
    @GetMapping
    public ResponseEntity<Page<Contribution>> getAll(@RequestParam(required = false) Long userId, 
        @RequestParam(required = false) Long goalId, @RequestParam(required = false) Long sourceId, 
        @RequestParam(defaultValue = "0") int page) {

            return service.getAll(userId, goalId, sourceId, page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contribution> getOne(@RequestParam(required = true) Long userId, @PathVariable Long id) {
        return service.getOne(userId, id);
    }

    @PostMapping
    public ResponseEntity<Contribution> createOne(@RequestParam(required = true) Long userId, @RequestParam(required = true) Long goalId, 
                                                @RequestParam(required = true) Long sourceId,@RequestBody ContributionDto dto) {
        return service.createOne(dto, userId, sourceId, goalId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contribution> updateOne(@RequestParam(required = true) Long userId, @PathVariable Long id, 
                                                @RequestBody ContributionDto dto) {   
                                                    
        return service.updateOne(id, userId, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOne(@RequestParam(required = true) Long userId, @PathVariable Long id) {
        return service.deleteOne(id, userId);
    }
    
}
