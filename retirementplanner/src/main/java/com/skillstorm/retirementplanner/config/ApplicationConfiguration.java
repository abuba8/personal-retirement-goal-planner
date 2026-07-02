package com.skillstorm.retirementplanner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.skillstorm.retirementplanner.repositories.UserRepository;
import com.skillstorm.retirementplanner.security.CustomUserDetails;


@Configuration
public class ApplicationConfiguration {
    /**
     * Wires up the authentication machinery as beans.
     *
     * Beans:
     * - userDetailsService(): loads a user by email OR username, wraps in CustomUserDetails
     * - authenticationProvider(): DaoAuthenticationProvider (modern constructor) + PasswordEncoder
     * - authenticationManager(): exposed so AuthenticationService can call it
     */
    private final UserRepository userRepository;

    // parameterized constructor
    public ApplicationConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Basically tells spring how to load a user for authentication
     * 
     * returns:
     * - UserDetailsService: looks up by email OR username, wraps the User,
     * throws UsernameNotFoundException if not found
     */
    @Bean
    UserDetailsService userDetailsService() {
        return identifier -> userRepository.findByEmailOrUsername(identifier, identifier)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * A component that actually checks the credentials.
     *
     * args:
     * - PasswordEncoder passwordEncoder: injected from PasswordConfig
     *
     * returns:
     * - AuthenticationProvider: DaoAuthenticationProvider using our user lookup + encoder
     */
    @Bean
    AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * It's the entry point that runs the providers.
     *
     * args:
     * - AuthenticationConfiguration config: Spring auth config
     *
     * returns:
     * - AuthenticationManager: pulled from the framework config
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}