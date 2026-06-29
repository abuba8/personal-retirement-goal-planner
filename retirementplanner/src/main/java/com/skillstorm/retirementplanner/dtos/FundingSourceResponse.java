package com.skillstorm.retirementplanner.dtos;

import com.skillstorm.retirementplanner.models.enums.SourceType;

public record FundingSourceResponse(Long id, String name, String institution, String notes, SourceType type) {

}
