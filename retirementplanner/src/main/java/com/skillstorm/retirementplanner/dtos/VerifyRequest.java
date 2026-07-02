package com.skillstorm.retirementplanner.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Incoming body for POST request /auth/verify
 * User submits the email they signed up with plus the 6-digit code emailed to them
 * 
 * fields:
 * - String email: valid email, required
 * - String verificationCode: the 6-digit code from the email, required
 */
public record VerifyRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid") String email,
    @NotBlank(message = "Verification code is required") String verificationCode
) {

}
