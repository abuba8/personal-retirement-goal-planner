package com.skillstorm.retirementplanner.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {
    /**
     * Config Class:
     * Exposes a PasswordEncoder bean so services can hash passwords.
     * BCrypt is salted and deliberately slow to resist brute-force attacks.
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}