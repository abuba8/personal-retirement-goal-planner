package com.skillstorm.retirementplanner.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * Income body for POST /auth/login
 * "identifier" accepts either the user email or username
 * 
 * fields:
 * - String identifier: email OR username, required
 * - String password: required
 */
public record LoginRequest(
    @NotBlank(message = "Email or username is required") String identifier,
    @NotBlank(message = "Password is required") String password) {
}