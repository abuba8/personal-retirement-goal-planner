package com.skillstorm.retirementplanner.dtos;

/**
 * DTO for user data. Used as the outgoing representation of a user
 * Has no passwordHash field, so that hash can never leak in a response.
 */
public record UserDto(Long id, String username, String email) {

}
