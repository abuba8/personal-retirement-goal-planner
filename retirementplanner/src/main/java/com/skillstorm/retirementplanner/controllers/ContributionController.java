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
@RequestMapping("/contributions")
@CrossOrigin({"http://127.0.0.1:5500", "http://localhost:4200",
"https://d2o5fqe44l9o0n.cloudfront.net", "https://d2oodvx207bj1j.cloudfront.net"
})
public class ContributionController {

    private final ContributionService service;
    private final SecurityUtils securityUtils;
    
    public ContributionController(ContributionService service, SecurityUtils securityUtils) {
        this.service = service;
        this.securityUtils = securityUtils;
    }

    /**
     * Get All endpoint takes in an optional goalId, sourceId, and the page number
     * @param goalId - used to filter contributions down to a single goal
     * @param sourceId - used to filter contributions down to a single funding source
     * @param page - tells how many pages the data should be split into
     * @return - returns a Response Entity status wrapped around a page of contributions for the current user
     */
    @GetMapping
    public ResponseEntity<Page<ContributionResponse>> getAll(@RequestParam(required = false) Long goalId,
        @RequestParam(required = false) Long sourceId, @RequestParam(value="page", defaultValue = "0") int page) {

            return service.getAll(this.securityUtils.getCurrentUserId(), goalId, sourceId, page);
    }

    /**
     * Get One endpoint takes in a contribution id
     * @param id - used to identify what contribution is being requested
     * @return - returns a contribution if it belongs to the current user
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContributionResponse> getOne(@PathVariable Long id) {
        return service.getOne(this.securityUtils.getCurrentUserId(), id);
    }

    /**
     * Create One endpoint takes in a goalId, sourceId, and a contribution dto
     * @param goalId - used to link the new contribution to a goal
     * @param sourceId - used to link the new contribution to a funding source
     * @param dto - populates a contribution entity with an amount, date, category, and notes
     * @return - returns a Response Entity status wrapped around the created contribution
     */
    @PostMapping
    public ResponseEntity<ContributionResponse> createOne(@RequestParam(required = true) Long goalId,
                                                @RequestParam(required = true) Long sourceId,@Valid @RequestBody ContributionRequest dto) {
        return service.createOne(dto, this.securityUtils.getCurrentUserId(), sourceId, goalId);
    }

    /**
     * Update One endpoint takes in a contribution id and a contribution dto
     * @param id - used to identify what contribution is being updated
     * @param goalId - optional; when provided, reassigns the contribution to this Goal
     * @param sourceId - optional; when provided, reassigns the contribution to this Funding Source
     * @param dto - populates a contribution entity with an amount, date, category, and notes
     * @return - returns a Response Entity status wrapped around the updated contribution
     */
    @PutMapping("/{id}")
    public ResponseEntity<ContributionResponse> updateOne(@PathVariable Long id, @RequestParam(required = false) Long goalId,
                            @RequestParam(required = false) Long sourceId, @Valid @RequestBody ContributionRequest dto) {

        return service.updateOne(id, this.securityUtils.getCurrentUserId(), dto, sourceId, goalId);
    }

    /**
     * Delete One endpoint takes in a contribution id
     * @param id - used to identify what contribution is being deleted
     * @return - returns a no content response if the contribution can be deleted, or a conflict if its date has already passed
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOne(@PathVariable Long id) {
        return service.deleteOne(id, this.securityUtils.getCurrentUserId());
    }
    
}
