package com.skillstorm.retirementplanner.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.skillstorm.retirementplanner.models.enums.AuthProvider;
import com.skillstorm.retirementplanner.models.enums.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "app_user")
public class User {
    /**
     * User Entity Class:
     * Maps to the app_user table ("user" is a reserved word in Postgres).
     * 
     * Fields: id, username, email, passwordHash
     * 
     * Default and parameterized constructor
     * Getters and Setters
     * toString method (deliberately excluding passwordHash so it never gets logged accidently!!!)
     */

    // Entity Columns
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @NotBlank(message="Username is required") @Size(max=100)
    @Column(name="username", nullable=false, unique=true, length=100)
    private String username;

    @NotBlank(message="Email is required")
    @Email(message="Email must be valid") @Size(max=255)
    @Column(name="email", nullable=false, unique=true, length=255)
    private String email;

    // Store a BCrypt hashed password only, never the plaintext one.
    // @NotBlank(message="Password is required") for google auth commented!
    @Column(name="password_hash", nullable=false, length=255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name="provider", nullable=false, length=20)
    private AuthProvider provider = AuthProvider.LOCAL;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable=false, length=20)
    private Role role = Role.USER;

    // enabled: usable only once true. Local signups start false; Google users true.
    @Column(name="enabled", nullable=false)
    private boolean enabled = false;

    @Column(name="verification_code", length=6)
    private String verificationCode;
    
    @Column(name="verification_code_expires_at")
    private LocalDateTime verificationCodeExpiresAt;

    @Column(name="verification_attempts", nullable=false)
    private Integer verificationAttempts = 0;

    public User() {
    }

    // parameterized constructor
    public User(Long id, String username, String email, String passwordHash) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // signup constructor (no id; id is generated on save)
    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // OAuth constructor
    public User(String username, String email, AuthProvider provider) {
        this.username = username;
        this.email = email;
        this.passwordHash = null;
        this.provider = provider;
        // this.enabled = true;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // toString method (no pass)
    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", email=" + email + "]";
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public LocalDateTime getVerificationCodeExpiresAt() {
        return verificationCodeExpiresAt;
    }

    public void setVerificationCodeExpiresAt(LocalDateTime verificationCodeExpiresAt) {
        this.verificationCodeExpiresAt = verificationCodeExpiresAt;
    }

    public int getVerificationAttempts() {
    return verificationAttempts == null ? 0 : verificationAttempts;
    }

    public void setVerificationAttempts(Integer verificationAttempts) {
        this.verificationAttempts = verificationAttempts == null ? 0 : verificationAttempts;
    }
}
