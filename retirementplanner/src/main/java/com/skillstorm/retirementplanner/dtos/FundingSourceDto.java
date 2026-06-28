package com.skillstorm.retirementplanner.dtos;

import com.skillstorm.retirementplanner.models.enums.SourceType;

public record FundingSourceDto(String name, String institution, String notes, SourceType type) {

}
