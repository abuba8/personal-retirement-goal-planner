package com.skillstorm.retirementplanner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class TempConfigFile {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // for now allowing all the https requests
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}