package com.skillstorm.retirementplanner.mappers;

import org.springframework.stereotype.Component;

import com.skillstorm.retirementplanner.dtos.FundingSourceResponse;
import com.skillstorm.retirementplanner.models.FundingSource;

@Component
public class FundingSourceMapper {

    public FundingSourceResponse toDto(FundingSource source) {
        return new FundingSourceResponse(source.getId(), source.getName(), source.getInstitution(), source.getNotes(), source.getSourceType());
    }
}
