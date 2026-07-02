package com.skillstorm.retirementplanner.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skillstorm.retirementplanner.dtos.LoginRequest;
import com.skillstorm.retirementplanner.dtos.RegisterRequest;
import com.skillstorm.retirementplanner.models.User;
import com.skillstorm.retirementplanner.repositories.UserRepository;

@Service
public class AuthService {

    /**
     * AuthService: 
     * All auth business logic is implemented here.
     * No email verification (as of now): accounts are usable right after signup. 
     * Throws RuntimeException on failures; the controller maps those to 400.
     *
     * Methods:
     * POST/Create:
     * - signup(RegisterRequest input)
     *
     * Auth:
     * - authenticate(LoginRequest input)
     */
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;

    // parameterized constructor
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
    }

    /**
     * authenticate:
     * It verifies login credentials (email OR username + password).
     *
     * args:
     * - LoginRequest input: identifier (email or username) + password
     *
     * returns:
     * - User: the authenticated user
     * 
     * throws:
     * - RuntimeException: if the user is not found or the password is wrong
     */
    public User authenticate(LoginRequest request) {
        User user = userRepository.findByEmailOrUsername(request.identifier(), request.identifier())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), request.password()));
        return user;
    }

    /**
     * signup:
     * creates a new user with a BCrypt-hashed password.
     *
     * args:
     * - RegisterRequest input: username, email, password
     *
     * returns:
     * - User: the saved user
     * 
     * throws:
     * - RuntimeException: if email or username already exists
     */
    public User signup(RegisterRequest request) {
        if(userRepository.existsByEmail(request.email())){
            throw new RuntimeException("Email already in use");
        }
        if(userRepository.existsByUsername(request.username())){
            throw new RuntimeException("Username already in use");
        }
        User user = new User(request.username(), request.email(), passwordEncoder.encode(request.password()));
        return userRepository.save(user);
    }

}
