package com.skillstorm.retirementplanner.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record GoalRequest(
        @NotBlank(message = "Goal name is required") @Size(max = 150) String name,
        @NotNull(message = "Target Retirement Age is required")
        @Positive(message = "Target Retirement Age must be greater than 0") Integer targetRetirementAge,
        @NotNull(message = "Target Amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Target Amount must be greater than 0")
        @Digits(integer = 13, fraction = 2) BigDecimal targetAmount,
        String notes) {
}