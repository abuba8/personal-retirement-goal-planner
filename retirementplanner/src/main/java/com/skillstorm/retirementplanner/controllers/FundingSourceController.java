package com.skillstorm.retirementplanner.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.retirementplanner.dtos.FundingSourceDto;
import com.skillstorm.retirementplanner.models.FundingSource;
import com.skillstorm.retirementplanner.services.FundingSourceService;
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

    public FundingSourceController(FundingSourceService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<FundingSource>> getAll(@RequestParam(required = false) Long userId, 
        @RequestParam(defaultValue = "0") int page) {

            return service.getAll(userId, page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FundingSource> getOne(@RequestParam(required = true) Long userId, @PathVariable Long id) {
        return service.getOne(userId, id);
    }

    @PostMapping
    public ResponseEntity<FundingSource> createOne(@RequestParam(required = true) Long userId, @RequestBody FundingSourceDto dto) {        
        return service.createOne(userId, dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FundingSource> updateOne(@RequestParam(required = true) Long userId, @PathVariable Long id, @RequestBody FundingSourceDto dto) {
        return service.updateOne(id, userId, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOne(@RequestParam(required = true) Long userId, @PathVariable Long id) {
        return service.deleteOne(id, userId);
    }      
}
