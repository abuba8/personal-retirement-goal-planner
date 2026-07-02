package com.skillstorm.retirementplanner.dtos;

import com.skillstorm.retirementplanner.models.enums.SourceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FundingSourceRequest(
        @NotBlank(message="Name cannot be empty") @Size(max=150) String name, 
        @Size(max=150) String institution, 
        String notes, 
        @NotNull(message="Source needs a type") SourceType type)  {
    
}
