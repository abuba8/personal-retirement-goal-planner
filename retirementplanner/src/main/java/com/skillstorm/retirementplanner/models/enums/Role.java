package com.skillstorm.retirementplanner.models.enums;

/**
 * Role Enum
 * - USER  : default, restricted to their own data
 * - ADMIN : full access, can hit admin-only endpoints (e.g. list all users)
 */
public enum Role {
    USER,
    ADMIN
}
