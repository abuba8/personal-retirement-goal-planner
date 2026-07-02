package com.skillstorm.retirementplanner.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Lightweight security config used ONLY by @WebMvcTest controller slice tests.
 *
 * The production SecurityConfiguration pulls in the JWT filter, the
 * authentication provider and the user-details service, none of which a web
 * slice should bootstrap. This permit-all chain replaces it so the controller
 * tests can focus on request/response behaviour, exactly as the old
 * TempConfigFile did before JWT was introduced.
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
