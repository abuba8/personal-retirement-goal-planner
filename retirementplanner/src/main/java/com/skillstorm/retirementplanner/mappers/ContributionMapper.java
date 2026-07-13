package com.skillstorm.retirementplanner.mappers;

import org.springframework.stereotype.Component;

import com.skillstorm.retirementplanner.dtos.ContributionResponse;
import com.skillstorm.retirementplanner.models.Contribution;

@Component
public class ContributionMapper {

    public ContributionResponse toDto(Contribution contribution) {
        return new ContributionResponse(contribution.getId(), contribution.getAmount(), contribution.getDate(), contribution.getCategory(), 
        contribution.getNotes(), contribution.getFundingSource().getId(), contribution.getGoal().getId());
    }
}
