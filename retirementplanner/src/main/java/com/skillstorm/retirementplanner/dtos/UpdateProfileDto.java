package com.skillstorm.retirementplanner.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating the current user's profile.
 * Both fields are optional: a null field means leave unchanged
 * The plaintext password (if provided) is hashed in the service before saving
 */
public record UpdateProfileDto(
        @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters") String username,
        @Email(message = "Email must be valid") @Size(max = 255) String email,
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters") String password) {
}
