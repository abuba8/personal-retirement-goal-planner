package com.skillstorm.retirementplanner.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.retirementplanner.dtos.FundingSourceRequest;
import com.skillstorm.retirementplanner.dtos.FundingSourceResponse;
import com.skillstorm.retirementplanner.security.SecurityUtils;
import com.skillstorm.retirementplanner.services.FundingSourceService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/sources")
@CrossOrigin({"http://127.0.0.1:5500", "http://localhost:4200"})
public class FundingSourceController {
    
    private final FundingSourceService service;
    private final SecurityUtils securityUtils;

    public FundingSourceController(FundingSourceService service, SecurityUtils securityUtils) {
        this.service = service;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<Page<FundingSourceResponse>> getAll(@RequestParam(value="page", defaultValue = "0") int page) {
        return this.service.getAll(this.securityUtils.getCurrentUserId(), page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FundingSourceResponse> getOne(@PathVariable("id") Long id) {
        return this.service.getOne(id, this.securityUtils.getCurrentUserId());
    }

    @PostMapping
    public ResponseEntity<FundingSourceResponse> createOne(@Valid @RequestBody FundingSourceRequest dto) {        
        return this.service.createOne(this.securityUtils.getCurrentUserId(), dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FundingSourceResponse> updateOne(@PathVariable Long id, @Valid @RequestBody FundingSourceRequest dto) {
        return this.service.updateOne(id, this.securityUtils.getCurrentUserId(), dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOne(@PathVariable Long id) {
        return this.service.deleteOne(id, this.securityUtils.getCurrentUserId());
    }      
}
