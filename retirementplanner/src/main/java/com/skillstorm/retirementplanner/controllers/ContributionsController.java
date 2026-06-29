package com.skillstorm.retirementplanner.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.retirementplanner.dtos.ContributionRequest;
import com.skillstorm.retirementplanner.dtos.ContributionResponse;
import com.skillstorm.retirementplanner.security.SecurityUtils;
import com.skillstorm.retirementplanner.services.ContributionService;

import jakarta.validation.Valid;

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
    private final SecurityUtils securityUtils;
    
    public ContributionsController(ContributionService service, SecurityUtils securityUtils) {
        this.service = service;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<Page<ContributionResponse>> getAll(@RequestParam(required = false) Long goalId, 
        @RequestParam(required = false) Long sourceId, @RequestParam(value="page", defaultValue = "0") int page) {

            return service.getAll(this.securityUtils.getCurrentUserId(), goalId, sourceId, page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContributionResponse> getOne(@PathVariable Long id) {
        return service.getOne(this.securityUtils.getCurrentUserId(), id);
    }

    @PostMapping
    public ResponseEntity<ContributionResponse> createOne(@RequestParam(required = true) Long goalId, 
                                                @RequestParam(required = true) Long sourceId,@Valid @RequestBody ContributionRequest dto) {
        return service.createOne(dto, this.securityUtils.getCurrentUserId(), sourceId, goalId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContributionResponse> updateOne(@PathVariable Long id, 
                                                @Valid @RequestBody ContributionRequest dto) {   
                                                    
        return service.updateOne(id, this.securityUtils.getCurrentUserId(), dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOne(@PathVariable Long id) {
        return service.deleteOne(id, this.securityUtils.getCurrentUserId());
    }
    
}
