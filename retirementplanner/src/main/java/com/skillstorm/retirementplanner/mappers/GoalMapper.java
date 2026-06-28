package com.skillstorm.retirementplanner.mappers;

import org.springframework.stereotype.Component;

import com.skillstorm.retirementplanner.dtos.GoalResponse;
import com.skillstorm.retirementplanner.models.Goal;

@Component
public class GoalMapper {
    /**
     * Mapper Class:
     * Converts a goal entity to a GoalDto so the entity (and its User relation)
     * never leaves the service layer. Kept manual and simple
     * 
     * Methods:
     * - toDto(Goal goal): builds the outgoing GoalDto from the entity
     */
    public GoalResponse toDto(Goal goal){
        return new GoalResponse(goal.getId(), goal.getName(), goal.getTargetRetirementAge(), goal.getTargetAmount(), goal.getNotes());
    }
}
