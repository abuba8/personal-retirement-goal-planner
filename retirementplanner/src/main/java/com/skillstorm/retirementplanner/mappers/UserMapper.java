package com.skillstorm.retirementplanner.mappers;

import org.springframework.stereotype.Component;

import com.skillstorm.retirementplanner.dtos.UserDto;
import com.skillstorm.retirementplanner.models.User;

@Component
public class UserMapper {
    /**
     * Mapper Class:
     * Converts a User entity to a UserDto. The DTO has no passwordhash field.
     * so the hash can never leak in a response
     * 
     * Methods:
     * - toDto(User user): builds the outgoing UserDto from the entity
     */
    public UserDto toDto(User user){
        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }
}
