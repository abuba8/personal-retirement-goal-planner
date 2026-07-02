package com.skillstorm.retirementplanner.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillstorm.retirementplanner.config.TestSecurityConfig;
import com.skillstorm.retirementplanner.dtos.UpdateProfileDto;
import com.skillstorm.retirementplanner.dtos.UserDto;
import com.skillstorm.retirementplanner.security.JwtAuthenticationFilter;
import com.skillstorm.retirementplanner.security.JwtService;
import com.skillstorm.retirementplanner.security.SecurityUtils;
import com.skillstorm.retirementplanner.services.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
/**
 * @@AutoConfigureMockMvc(addFilters = false)
 * Disables the servlet Filter chain (most importantly Spring Security's
 * springSecurityFilterChain) for MockMvc in this slice test.
 *
 * WHY THIS IS HERE:
 * In a real request, servlet filters run BEFORE the controller. Spring
 * Security installs one such filter chain that authenticates/authorizes the
 * request first, and only then lets it continue to the @RestController method.
 * In this @WebMvcTest slice there is no working security setup (no real JWT
 * filter, no authenticated principal on the mock request), so that filter
 * chain would intercept every request, fail to authenticate it, and
 * short-circuit with an empty 200 response — the controller method would
 * never be invoked. The tell-tale symptom is "Handler = null" in the MockMvc
 * output, meaning no controller was reached.
 *
 * WHAT addFilters = false DOES:
 * It tells MockMvc NOT to register the servlet filters. With the security
 * filter chain removed from the path, the request goes straight to the
 * DispatcherServlet, matches the controller mapping, and runs the actual
 * handler method. The test then sees the real status codes (201/404/204),
 * the real JSON body, and @Valid validation results that the assertions
 * expect.
 *
 * WHY IT'S CORRECT (NOT A HACK):
 * A controller slice test is meant to verify the WEB layer — routing,
 * request/response (de)serialization, validation, and status codes — not
 * security. Turning filters off keeps the test focused on exactly that.
 *
 * TRADE-OFF:
 * With filters disabled, these tests can no longer catch security
 * regressions (e.g. an endpoint left unprotected), because no security runs
 * here. 
 */

@Import(TestSecurityConfig.class)
@DisplayName("UserController - Web Layer Tests")
class UserControllerTest {
    /**
     * UserControllerTest Class:
     * Web-layer (slice) tests for UserController using MockMvc.
     * The service + SecurityUtils are mocked; we check HTTP behaviour only.
     *
     * Tests:
     * GET /users:
     * - returnsProfile: 200 + profile, and NO password field leaks
     *
     * PUT /users:
     * - returnsUpdated: 200 + updated profile
     * - returns400WhenInvalid: 400 on a bad body (@Valid)
     */

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private SecurityUtils securityUtils;

    // JWT collaborators are mocked so the security graph resolves in the slice.
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    private UserDto testDto;

    @BeforeEach
    void dataInit(){
        testDto = new UserDto(1L, "alice", "alice@example.com");
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
    }

    @Nested
    @DisplayName("GET /users")
    class GetCurrentUser{
        /**
         * Returns the current user's profile, and must NOT expose the password.
         *
         * setup:
         * - getCurrentUserProfile(1) -> 200 + testDto
         *
         * assert:
         * - HTTP 200, username = "alice", and no passwordHash field in the JSON
         */
        @Test
        @DisplayName("200 OK with the current user's profile")
        void returnsProfile() throws Exception {
            when(userService.getCurrentUserProfile(1L)).thenReturn(ResponseEntity.ok(testDto));

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("alice"))
                    // the response must NOT leak a password field
                    .andExpect(jsonPath("$.passwordHash").doesNotExist());
        }
    }

    @Nested
    @DisplayName("PUT /users")
    class UpdateCurrentUser{
        /**
         * A valid update body -> 200 with the updated profile.
         *
         * setup:
         * - updateProfile(anyLong, any) -> 200 + testDto
         *
         * assert:
         * - HTTP 200, JSON id = 1
         */
        @Test
        @DisplayName("200 OK with the updated profile")
        void returnsUpdated() throws Exception {
            when(userService.updateProfile(any(Long.class), any(UpdateProfileDto.class)))
                    .thenReturn(ResponseEntity.ok(testDto));

            UpdateProfileDto body = new UpdateProfileDto(null, "alice2@example.com", null);

            mockMvc.perform(put("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        // body that fails validation is rejected before the service asserts 400
        @Test
        @DisplayName("400 BAD REQUEST when the body fails validation")
        void returns400WhenInvalid() throws Exception{
            // bad email + too-short password -> @Valid rejects before the service
            UpdateProfileDto invalid = new UpdateProfileDto(null, "not-an-email", "short");

            mockMvc.perform(put("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());
        }
    }
}