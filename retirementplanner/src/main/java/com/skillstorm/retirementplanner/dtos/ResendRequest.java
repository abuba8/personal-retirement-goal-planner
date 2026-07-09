package com.skillstorm.retirementplanner.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Incoming body for POST request /auth/resend
 * Used when the first code expired or never arrived then a fresh code gets emailed
 * 
 * fields:
 * - String email: valid email, required
 */
public record ResendRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid") String email
) {

}
