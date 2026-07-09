package com.skillstorm.retirementplanner.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.skillstorm.retirementplanner.models.User;

public class CustomUserDetails implements UserDetails {
    /**
     * Adapter that lets spring security use our plain user entity.
     * We're keeping User as a pure JPA entity, and wrap it here instead of making
     * User implement UserDetails directly.
     * 
     * - getUsername(): returns the email (this is the jwt subject/principle name)
     * - getPassword(): returns the stored bcrypt hash
     * - isEnabled(): returns true (no email vertification step for now), will do next
     * - getUser() & getId(): uses the wrapped user so securityUtils can read the id
     */

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // returns the wrapper user entity
    public User getUser() {
        return user;
    }

    // Accessor for wrapped user id, returns user id
    public Long getId() {
        return user.getId();
    }

    /**
     * Must include method, when implementing UserDetails
     * Roles/permission for this user, no roles in this app as of now
     * returns empty list
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // No roles in this app yet; return an empty authority list.
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    // password spring security compares against during login, and returns the stored bcrypt password hash
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    // principle name for spring security, using email as of now. 
    @Override
    public String getUsername() {
        // email is out principle name/ token subject
        return user.getEmail();
    }

    // check if account is still valid (not expired)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // check if the account is unlocked
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // check if the credentials (password) are still valid
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // check if the account is active/enabled
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
