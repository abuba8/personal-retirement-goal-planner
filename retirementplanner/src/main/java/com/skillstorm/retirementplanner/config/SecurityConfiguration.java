package com.skillstorm.retirementplanner.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.DispatcherType;   // fix

import com.skillstorm.retirementplanner.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // enables preauthorize for admin roles/responsibilities
public class SecurityConfiguration {
    /**
     * It is the central security policy file that defines who can hit what, makes the app stateless, and plugs in our JWT filter.
     *
     * Beans:
     * - securityFilterChain(http): the rules: /auth/** public, everything else authenticated
     * - corsConfigurationSource(): allowed origins/methods/headers for the frontend
     *
     * Key choices:
     * - CSRF disabled (we use header tokens, not cookies)
     * - STATELESS sessions (each request carries its own JWT)
     * - JwtAuthenticationFilter runs before UsernamePasswordAuthenticationFilter
     */
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // parameterized constructor
    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * builds the HTTP security rules for the whole app.
     *
     * args:
     * - HttpSecurity http: the security builder
     *
     * returns:
     * - SecurityFilterChain: the configured chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()   // <-- was getting 403 on all bad requests, this fixes
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * It allows the frontend origins to call this API from a browser.
     *
     * returns:
     * - CorsConfigurationSource: allowed origins/methods/headers for "/**"
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // build the CORS policy
        CorsConfiguration configuration = new CorsConfiguration();
<<<<<<< HEAD
        configuration.setAllowedOrigins(List.of("http://127.0.0.1:5500", "http://localhost:4200", "https://d2o5fqe44l9o0n.cloudfront.net", "https://d2oodvx207bj1j.cloudfront.net"));
=======
        configuration.setAllowedOrigins(List.of("http://127.0.0.1:5500", "http://localhost:4200", 
        "https://d2o5fqe44l9o0n.cloudfront.net", "https://d2oodvx207bj1j.cloudfront.net"));
>>>>>>> origin/main
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        // apply this policy to every path
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}