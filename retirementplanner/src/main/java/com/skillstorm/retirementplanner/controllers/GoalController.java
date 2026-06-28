package com.skillstorm.retirementplanner.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.retirementplanner.dtos.GoalRequest;
import com.skillstorm.retirementplanner.dtos.GoalResponse;
import com.skillstorm.retirementplanner.security.SecurityUtils;
import com.skillstorm.retirementplanner.services.GoalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/goals")
@CrossOrigin({"http://127.0.0.1:5500", "http://localhost:4200"})
public class GoalController {
    /**
     * Controller Class:
     * Just receiving the requests, no business logic implemented in this class.
     * Validation is being checked
     * Returning ReponseEntity (To check which response entity is being returned, check service layer)
     * 
     * Create Method:
     * - createGoal -> POST request, creates a goal for the current user and returns a response entity (received from service layer)
     * 
     * Get Methods:
     * - getAllGoals -> GET request, fetches all goals for the current user and returns response entity
     * - getGoalById -> GET request, fetches the goal by ID for the current user
     * 
     * Update Method:
     * - updateGoalById -> PUT request, updates the goal by ID and returns response entity
     * 
     * Delete Method:
     * - deleteGoalById -> DELETE request, deletes the goal by ID and returns response entity
     * 
     */
    private final GoalService goalService;
    private final SecurityUtils securityUtils;

    // 
    public GoalController(GoalService goalService, SecurityUtils securityUtils) {
        this.goalService = goalService;
        this.securityUtils = securityUtils;
    }

    // Create goal request, using POST mapping
    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@Valid @RequestBody GoalRequest goalDto) {
        return this.goalService.createGoal(this.securityUtils.getCurrentUserId(), goalDto); // Create a goal for the current user
    }

    // Get requests
    // Get all goals for the current user
    // @GetMapping
    // public ResponseEntity<List<GoalDto>> getAllGoals() {
    //     return this.goalService.getAllGoals(this.securityUtils.getCurrentUserId()); 
    // }

    // Get all goals for the current user with pagination
    @GetMapping
    public ResponseEntity<Page<GoalResponse>> getAllGoals(@RequestParam(value = "page", defaultValue = "0") int page) {
        return this.goalService.getAllGoalsPaged(this.securityUtils.getCurrentUserId(), page);
    }

    // Get request by id for the current user
    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoalById(@PathVariable("id") Long id) {
        return this.goalService.getGoalById(this.securityUtils.getCurrentUserId(), id);
    }

    // Update request using Put Mapping
    // Update by id
    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoalById(@PathVariable("id") Long id, @Valid @RequestBody GoalRequest goalDto) {
        return this.goalService.updateGoalById(this.securityUtils.getCurrentUserId(), id, goalDto);
    }

    // Delete request using Delete mapping
    // delete by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoalById(@PathVariable("id") Long id) {
        return this.goalService.deleteGoalById(this.securityUtils.getCurrentUserId(), id);
    }
}