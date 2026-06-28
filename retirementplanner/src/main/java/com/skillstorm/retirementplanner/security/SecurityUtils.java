package com.skillstorm.retirementplanner.security;
 
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public Long getCurrentUserId(){
        // sending user with id=1 for now, for testing purposes
        // will replace this user after implementing JWT Auth, and pass the current user here
        return 1L;
    }
}