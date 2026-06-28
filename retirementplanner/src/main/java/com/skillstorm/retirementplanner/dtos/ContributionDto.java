package com.skillstorm.retirementplanner.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.skillstorm.retirementplanner.models.enums.ContributionCategory;

public record ContributionDto(BigDecimal amount, LocalDate date, 
    ContributionCategory category, String notes) { }
