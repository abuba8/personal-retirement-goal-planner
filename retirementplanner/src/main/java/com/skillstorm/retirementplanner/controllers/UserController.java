package com.skillstorm.retirementplanner.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.skillstorm.retirementplanner.dtos.UpdateProfileDto;
import com.skillstorm.retirementplanner.dtos.UserDto;
import com.skillstorm.retirementplanner.security.SecurityUtils;
import com.skillstorm.retirementplanner.services.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
<<<<<<< HEAD
@CrossOrigin({"http://127.0.0.1:5500", "http://localhost:4200", "https://d2o5fqe44l9o0n.cloudfront.net", "https://d2oodvx207bj1j.cloudfront.net"})
=======
@CrossOrigin({"http://127.0.0.1:5500", "http://localhost:4200",
"https://d2o5fqe44l9o0n.cloudfront.net", "https://d2oodvx207bj1j.cloudfront.net"
})
>>>>>>> origin/main
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers(){
        return this.userService.getAllUserProfile();
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

    // DELETE Request: delete the current user's account
    @DeleteMapping
    public ResponseEntity<Void> deleteCurrentUser(){
        return this.userService.deleteCurrentUser(this.securityUtils.getCurrentUserId());
    }
}
