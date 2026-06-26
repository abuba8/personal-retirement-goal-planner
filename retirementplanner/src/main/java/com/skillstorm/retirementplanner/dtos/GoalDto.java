package com.skillstorm.retirementplanner.dtos;

import java.math.BigDecimal;

/**
 * Transfer data between layers of the application
 * Carries goal data in and out. The owning user is NOT in the DTO 
 * it comes from the authenticated request, never from the client body
 */
public record GoalDto(Long id, String name, Integer targetRetirementAge, BigDecimal targetAmount, String notes) {

}
