package com.skillstorm.retirementplanner.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.retirementplanner.dtos.LoginRequest;
import com.skillstorm.retirementplanner.dtos.LoginResponse;
import com.skillstorm.retirementplanner.dtos.RegisterRequest;
import com.skillstorm.retirementplanner.models.User;
import com.skillstorm.retirementplanner.security.CustomUserDetails;
import com.skillstorm.retirementplanner.security.JwtService;
import com.skillstorm.retirementplanner.services.AuthService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@CrossOrigin({"http://127.0.0.1:5500", "http://localhost:4200"})
public class AuthController {
    /**
     * AuthController:
     * Just receiving the requests, no business logic implemented in this class
     * Validation is being checked @Valid
     * Returning ResponseEntity (business logic is in AuthenticationService / JwtService)
     *
     * Create Method:
     * - register -> POST request, signs a new user up and returns 201
     *
     * Auth Method:
     * - authenticate -> POST request, verifies login and returns a JWT
     */
    private final JwtService jwtService;
    private final AuthService authService;

    // parameterized constructor
    public AuthController(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    /**
     * POST request to create a new account.
     *
     * args:
     * - RegisterRequest request: username, email, password (validated)
     *
     * returns:
     * - 201: signup successful
     * - 400: if email/username already in use (from the service)
     */
    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        try{
            User registered = authService.signup(request);
            return ResponseEntity.status(201).body("Signup successful for " + registered.getUsername());
        } catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * POST request to log in and receive a JWT.
     *
     * args:
     * - LoginRequest request: identifier (email or username) + password
     *
     * returns:
     * - 200: LoginResponse { token, expiresIn }
     * - 400: if credentials are invalid (from the service)
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody LoginRequest request){
        try{
            User user = authService.authenticate(request);
            String jwt = jwtService.generateToken(new CustomUserDetails(user));
            return ResponseEntity.ok(new LoginResponse(jwt, jwtService.getExpirationTime()));
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
}
