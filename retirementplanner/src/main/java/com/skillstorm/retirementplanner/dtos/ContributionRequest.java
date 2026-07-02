package com.skillstorm.retirementplanner.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.skillstorm.retirementplanner.models.enums.ContributionCategory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ContributionRequest(
        @NotNull(message="Need to contribute a positive amount") @Positive BigDecimal amount, 
        @NotNull(message="Need to provide a date for Contribution") LocalDate date, 
        @NotNull(message="All Contributions need a category") ContributionCategory category, 
        String notes) {

}
