package com.skillstorm.retirementplanner.models.enums;

/**
 * AuthProvider Enum (Future OAuth task)
 * - Local: username + password (bcrypt hashstored)
 * - Google: created via Login with google has no local pass
 */
public enum AuthProvider {
    LOCAL,
    GOOGLE
}
