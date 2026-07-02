package com.skillstorm.retirementplanner.dtos;

/**
 * Outgoing body for successful POST request /auth/login
 * Carries the signed JWT and how long it stays valid in milliseconds
 * 
 * fields:
 * - String token: the signed JWT
 * - long expiresIn: token lifetime in milliseconds
 */
public record LoginResponse(String token, long expiresIn) {
}