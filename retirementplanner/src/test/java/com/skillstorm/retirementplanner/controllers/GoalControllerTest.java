package com.skillstorm.retirementplanner.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillstorm.retirementplanner.config.TestSecurityConfig;
import com.skillstorm.retirementplanner.dtos.GoalRequest;
import com.skillstorm.retirementplanner.dtos.GoalResponse;
import com.skillstorm.retirementplanner.security.JwtAuthenticationFilter;
import com.skillstorm.retirementplanner.security.JwtService;
import com.skillstorm.retirementplanner.security.SecurityUtils;
import com.skillstorm.retirementplanner.services.GoalService;

@WebMvcTest(GoalController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
@DisplayName("GoalController - Web Layer Tests")
class GoalControllerTest {
    /**
     * GoalControllerTest Class:
     * Web-layer (slice) tests for GoalController using MockMvc.
     * The service + SecurityUtils are mocked; we only check HTTP behaviour
     * (status codes, JSON body, validation), not business logic.
     *
     * Tests:
     * POST /goals:
     * - returnsCreated: 201 + created goal
     * - returns400WhenInvalid: 400 on a bad body (@Valid)
     *
     * GET /goals:
     * - returnsPage: 200 + page of goals
     *
     * GET /goals/{id}:
     * - returnsGoal: 200 + goal
     * - returns404: 404 when the service says not found
     *
     * DELETE /goals/{id}:
     * - returns204: 204 on delete
     */

    // sends fake https requests to the controller
    @Autowired
    private MockMvc mockMvc;

    // turns Java objects into JSON request bodies
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GoalService goalService;

    @MockitoBean
    private SecurityUtils securityUtils;

    // JWT collaborators are mocked so the security graph resolves in the slice.
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    private GoalResponse testResponse;

    @BeforeEach
    void dataInit(){
        testResponse = new GoalResponse(1L, "Early Retirement", 60,
                new BigDecimal("1000000.00"), "Main goal.");
        // controller always asks SecurityUtils who the current user is
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
    }

    @Nested
    @DisplayName("POST /goals")
    class CreateGoal{
        /**
         * Valid body: controller returns whatever the service returns (201 + goal).
         *
         * setup:
         * - createGoal(1, any) -> 201 + testResponse
         *
         * assert:
         * - HTTP 201, JSON id = 1 and name = "Early Retirement"
         */
        @Test
        @DisplayName("201 CREATED with the new goal for a valid body")
        void returnsCreated() throws Exception{
            when(goalService.createGoal(eq(1L), any(GoalRequest.class)))
                    .thenReturn(ResponseEntity.status(201).body(testResponse));

            GoalRequest body = new GoalRequest("Early Retirement", 60,
                    new BigDecimal("1000000.00"), "Main goal.");

            mockMvc.perform(post("/goals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Early Retirement"));
        }

        @Test
        @DisplayName("400 BAD REQUEST when the body fails validation")
        void returns400WhenInvalid() throws Exception{
            // blank name, non-positive age + amount -> @Valid rejects before the service
            GoalRequest invalid = new GoalRequest("", -1, new BigDecimal("-5.00"), null);

            mockMvc.perform(post("/goals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /goals")
    class GetAllGoals{
        /**
         * Returns the page the service produces as JSON.
         *
         * setup:
         * - getAllGoalsPaged(anyLong, 0) -> 200 + a one-goal page
         *
         * assert:
         * - HTTP 200, first item's name is "Early Retirement"
         */
        @Test
        @DisplayName("200 OK with a page of goals")
        void returnsPage() throws Exception {
            Page<GoalResponse> page = new PageImpl<>(List.of(testResponse), PageRequest.of(0, 10), 1);
            when(goalService.getAllGoalsPaged(anyLong(), eq(0)))
                    .thenReturn(ResponseEntity.ok(page));

            mockMvc.perform(get("/goals").param("page", "0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].name").value("Early Retirement"));
        }
    }

    @Nested
    @DisplayName("GET /goals/{id}")
    class GetGoalById{
        /**
         * Found goal -> 200 with the goal JSON.
         *
         * setup:
         * - getGoalById(1,1) -> 200 + testResponse
         *
         * assert:
         * - HTTP 200, JSON id = 1
         */
        @Test
        @DisplayName("200 OK with the goal when found")
        void returnsGoal() throws Exception{
            when(goalService.getGoalById(1L, 1L)).thenReturn(ResponseEntity.ok(testResponse));

            mockMvc.perform(get("/goals/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("404 NOT FOUND when the service returns not found")
        void returns404() throws Exception{
            when(goalService.getGoalById(1L, 999L)).thenReturn(ResponseEntity.notFound().build());

            mockMvc.perform(get("/goals/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /goals/{id}")
    class DeleteGoalById{
        /**
         * Successful delete -> 204 No Content.
         *
         * setup:
         * - deleteGoalById(1,1) -> 204
         *
         * assert:
         * - HTTP 204
         */
        @Test
        @DisplayName("204 NO CONTENT on a successful delete")
        void returns204() throws Exception {
            when(goalService.deleteGoalById(1L, 1L)).thenReturn(ResponseEntity.noContent().build());

            mockMvc.perform(delete("/goals/1"))
                    .andExpect(status().isNoContent());
        }
    }
}