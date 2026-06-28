package com.skillstorm.retirementplanner.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import com.skillstorm.retirementplanner.dtos.UpdateProfileDto;
import com.skillstorm.retirementplanner.dtos.UserDto;
import com.skillstorm.retirementplanner.models.User;
import com.skillstorm.retirementplanner.security.SecurityUtils;
import com.skillstorm.retirementplanner.services.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
@CrossOrigin({"http://127.0.0.1:5500", "http://localhost:4200"})
public class UserController {
    /**
     * Controller Class:
     * Just receiving the requests, no business logic implemented in this class.
     * Validation is being checked
     * Returning ReponseEntity (To check which response entity is being returned, check service layer)
     * 
     * Get Methods:
     * - getCurrentUser -> GET request, fetches the current user and returns response entity
     * 
     * Update Method:
     * - updatUserById -> PUT request, updates the current user
     * 
     */
    private final UserService userService;
    private final SecurityUtils securityUtils;

    public UserController(UserService userService, SecurityUtils securityUtils) {
        this.userService = userService;
        this.securityUtils = securityUtils;
    }

    // GET Request: get current user profile
    @GetMapping()
    public ResponseEntity<UserDto> getCurrentUser() {
        return this.userService.getCurrentUserProfile(this.securityUtils.getCurrentUserId());
    }
    
    // PUT Request: Update current user (things to check: try without passing id, once the currentUser functionality is up and running)
    @PutMapping()
    public ResponseEntity<UserDto> updateCurrentUser(@Valid @RequestBody UpdateProfileDto dto){
        return this.userService.updateProfile(this.securityUtils.getCurrentUserId(), dto);
    }
}
