package com.skillstorm.retirementplanner.dtos;

/**
 * DTO for updating the current user's profile.
 * Both fields are optional: a null field means leave unchanged
 * The plaintext password (if provided) is hashed in the service before saving
 */
public record UpdateProfileDto(String username, String email, String password) {

}
