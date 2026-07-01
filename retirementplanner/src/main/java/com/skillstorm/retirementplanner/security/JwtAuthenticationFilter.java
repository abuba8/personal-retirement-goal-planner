package com.skillstorm.retirementplanner.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Runs once per request. If a valid "Authorization: Bearer <jwt>" header is
 * present, it loads the user and places an authenticated token in the
 * SecurityContext so downstream code (controllers, SecurityUtils) can read it.
 * Requests without a token simply pass through unauthenticated.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    /**
     * Runs once on every request. If a valid "Authorization: Bearer <jwt>"
     * header is present, it loads the user and marks them authenticated for
     * this request (by putting them in the SecurityContext).
     * Requests with no token just pass through as anonymous.
     *
     * Methods:
     * - doFilterInternal(request, response, filterChain) -> the per-request logic
     */
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // parameterized constructor
    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }


    /**
     * doFilterInternal:
     * Reads the Bearer token, validates it, and authenticates the request.
     *
     * args:
     * - HttpServletRequest request: incoming request
     * - HttpServletResponse response: outgoing response
     * - FilterChain filterChain: the remaining filters to run
     *
     * return:
     * - void: on success continues the chain (authenticated or anonymous);
     *   on error delegates to the exception resolver
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // fetch the Authorization header
        final String authHeader = request.getHeader("Authorization");

        // no bearer token, then let the request continue
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // strip bearer prefex to get raw token
            final String jwt = authHeader.substring(7);
            // read email out of token
            final String userEmail = jwtService.extractUsername(jwt);

            // check whatever is currently in context 
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // only authenticate if we have email and no one is set yet
            if (userEmail != null && authentication == null) {
                // load the user from db via email
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // token must match this user and shouldnt be expired
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // build an authenticated token
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    // attach request details like IP, session, id etc.
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // mark user authenticated for this request
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            // continue the filter chain
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
