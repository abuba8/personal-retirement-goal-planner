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
<<<<<<< HEAD
@CrossOrigin({"http://127.0.0.1:5500", "http://localhost:4200", "https://d2o5fqe44l9o0n.cloudfront.net", "https://d2oodvx207bj1j.cloudfront.net"})
=======
@CrossOrigin({"http://127.0.0.1:5500", "http://localhost:4200",
"https://d2o5fqe44l9o0n.cloudfront.net", "https://d2oodvx207bj1j.cloudfront.net"
})
>>>>>>> origin/main
public class FundingSourceController {
    
    private final FundingSourceService service;
    private final SecurityUtils securityUtils;

    public FundingSourceController(FundingSourceService service, SecurityUtils securityUtils) {
        this.service = service;
        this.securityUtils = securityUtils;
    }

    /**
     * Get All endpoint takes in the page number
     * @param page - tells how many pages the data should be split into
     * @return - returns a Response Entity status wrapped around a page of funding sources for the current user
     */
    @GetMapping
    public ResponseEntity<Page<FundingSourceResponse>> getAll(@RequestParam(value="page", defaultValue = "0") int page) {
        return this.service.getAll(this.securityUtils.getCurrentUserId(), page);
    }

    /**
     * Get One endpoint takes in a funding source id
     * @param id - used to identify what funding source is being requested
     * @return - returns a funding source if it belongs to the current user
     */
    @GetMapping("/{id}")
    public ResponseEntity<FundingSourceResponse> getOne(@PathVariable("id") Long id) {
        return this.service.getOne(id, this.securityUtils.getCurrentUserId());
    }

    /**
     * Create One endpoint takes in a funding source dto
     * @param dto - populates a funding source entity with a name, institution, notes, and source type
     * @return - returns a Response Entity status wrapped around the created funding source
     */
    @PostMapping
    public ResponseEntity<FundingSourceResponse> createOne(@Valid @RequestBody FundingSourceRequest dto) {
        return this.service.createOne(this.securityUtils.getCurrentUserId(), dto);
    }

    /**
     * Update One endpoint takes in a funding source id and a funding source dto
     * @param id - used to identify what funding source is being updated
     * @param dto - populates a funding source entity with a name, institution, notes, and source type
     * @return - returns a Response Entity status wrapped around the updated funding source
     */
    @PutMapping("/{id}")
    public ResponseEntity<FundingSourceResponse> updateOne(@PathVariable Long id, @Valid @RequestBody FundingSourceRequest dto) {
        return this.service.updateOne(id, this.securityUtils.getCurrentUserId(), dto);
    }

    /**
     * Delete One endpoint takes in a funding source id
     * @param id - used to identify what funding source is being deleted
     * @return - returns a no content response if the source can be deleted, or a conflict if contributions still reference it
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOne(@PathVariable Long id) {
        return this.service.deleteOne(id, this.securityUtils.getCurrentUserId());
    }
}
