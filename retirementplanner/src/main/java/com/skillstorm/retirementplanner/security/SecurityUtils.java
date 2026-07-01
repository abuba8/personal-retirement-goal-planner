package com.skillstorm.retirementplanner.security;
 
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    /**
     * SecurityUtils Class:
     * Helper function for the rest of the app, that uses to find the current logged-in user.
     * The JwtAuthenticationFilter puts a CustomUserDetails in the SecurityContext,
     * so this now returns the REAL user id
     * 
     * Methods:
     * - getCurrentUserId(): the id of the authenticated user
     */

    /**
     * Reads the current user's id from the security context.
     *
     * returns:
     * - Long: the authenticated user id
     * 
     * throws:
     * - IllegalStateException: if no authenticated user is on the request
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }

        // No authenticated user on the request.
        throw new IllegalStateException("No authenticated user found in the security context");
    }
}