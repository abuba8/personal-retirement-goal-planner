package com.skillstorm.retirementplanner.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid") String email
) {

}
