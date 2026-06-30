package com.skillstorm.retirementplanner.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillstorm.retirementplanner.dtos.ContributionRequest;
import com.skillstorm.retirementplanner.dtos.ContributionResponse;
import com.skillstorm.retirementplanner.models.enums.ContributionCategory;
import com.skillstorm.retirementplanner.security.SecurityUtils;
import com.skillstorm.retirementplanner.services.ContributionService;

@WebMvcTest(ContributionsController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Contribution Controller - Web Layer Tests")
public class ContributionControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ContributionService service;

    @MockitoBean
    private SecurityUtils securityUtils;

    private ContributionResponse testResponse;
    private Pageable testPage;
    private List<ContributionResponse> contributions;
    private Page<ContributionResponse> contributionPage;
    private ContributionRequest testDto;

    @BeforeEach
    void dataInit() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        testResponse = new ContributionResponse(1L, new BigDecimal("500.00"), LocalDate.now().plusDays(1), 
            ContributionCategory.EMPLOYEE_SALARY_DEFERRAL, "January paycheck contribution.");

        testPage = PageRequest.of(0, 6);
        contributions = List.of(testResponse, testResponse, testResponse, testResponse, testResponse, testResponse);
        contributionPage = new PageImpl<>(contributions, testPage, contributions.size());
        testDto = new ContributionRequest(new BigDecimal("500.00"), LocalDate.of(2026, Month.JANUARY, 15), ContributionCategory.EMPLOYEE_SALARY_DEFERRAL, "January paycheck contribution.");
    }

    @Nested
    @DisplayName("GET /contributions")
    class GetAllContributions {

        @Test
        @DisplayName("200 OK with a Page of all Contributions with no User")
        void returnsAllContributions() throws Exception {
            when(securityUtils.getCurrentUserId()).thenReturn(null);
            when(service.getAll(null, null, null, 0)).thenReturn(ResponseEntity.ok(contributionPage));

            mockMvc.perform(get("/contributions")).andExpect(status().isOk());
        }

        @Test
        @DisplayName("200 OK with a Page of all Contributions of a given User")
        void returnsAllUsersContributions() throws Exception {
            when(service.getAll(1L, null, null, 0)).thenReturn(ResponseEntity.ok(contributionPage));

            mockMvc.perform(get("/contributions")).andExpect(status().isOk());
        }

        @Test
        @DisplayName("200 OK with a Page of all Contributions using the given Goal of a given User")
        void returnsAllUsersContributionsByGoal() throws Exception {
            when(service.getAll(1L, 2L, null, 0)).thenReturn(ResponseEntity.ok(contributionPage));

            mockMvc.perform(get("/contributions").param("goalId", "2")).andExpect(status().isOk());
        }

        @Test
        @DisplayName("200 OK with a Page of all Contributions using the given Source of a given User")
        void returnsAllUsersContributionsBySource() throws Exception {
            when(service.getAll(1L, null, 3L, 0)).thenReturn(ResponseEntity.ok(contributionPage));

            mockMvc.perform(get("/contributions").param("sourceId", "3")).andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /contributions/{id}")
    class GetOneContribution {
        
        @Test
        @DisplayName("404 NOT FOUND when User doesn't have the given Contribution")
        void returnsNoContributionIfNotFound() throws Exception {
            when(service.getOne(1L, 2L)).thenReturn(ResponseEntity.notFound().build());

            mockMvc.perform(get("/contributions/2")).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("200 OK if User has the matching Contribution")
        void returnsOneContributionIfFound() throws Exception {
            when(service.getOne(1L, 1L)).thenReturn(ResponseEntity.ok(testResponse));

            mockMvc.perform(get("/contributions/1")).andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("POST /contributions")
    class CreateContribution {

        @Test
        @DisplayName("201 CREATED if all Values are provided")
        void returnsCreatedContributionIfAllFound() throws Exception {
            when(service.createOne(testDto, 1L, 3L, 2L)).thenReturn(ResponseEntity.status(201).body(testResponse));

            mockMvc.perform(post("/contributions")
            .param("goalId", "2").param("sourceId", "3")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testDto)))
            .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("404 NOT FOUND if User Not Found")
        void returnsNotFoundIfUserNotFound() throws Exception {
            when(securityUtils.getCurrentUserId()).thenReturn(2L);
            when(service.createOne(testDto, 2L, 3L, 2L)).thenReturn(ResponseEntity.status(404).build());

            mockMvc.perform(post("/contributions")
            .param("goalId", "2").param("sourceId", "3")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testDto)))
            .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("404 NOT FOUND if Goal Not Found")
        void returnsNotFoundIfGoalNotFound() throws Exception {
            when(service.createOne(testDto, 1L, 2L, 4L)).thenReturn(ResponseEntity.status(404).build());

            mockMvc.perform(post("/contributions")
            .param("goalId", "4").param("sourceId", "2")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testDto)))
            .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("404 NOT FOUND if Source Not Found")
        void returnsNotFoundIfSourceNotFound() throws Exception {
            when(service.createOne(testDto, 1L, 4L, 2L)).thenReturn(ResponseEntity.status(404).build());

            mockMvc.perform(post("/contributions")
            .param("goalId", "2").param("sourceId", "4")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testDto)))
            .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /contributions/{id}")
    class UpdateContributions {
    
        @Test
        @DisplayName("404 NOT FOUND if User doesn't have the given Contribution to Update")
        void returnsNoUpdatedContributionIfNotFound() throws Exception {
            when(service.updateOne(2L, 1L, testDto)).thenReturn(ResponseEntity.notFound().build());

            mockMvc.perform(put("/contributions/2")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testDto)))
            .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("200 OK if Contribution is Found for Updating")
        void returnsUpdatedContributionIfFound() throws Exception {
            when(service.updateOne(1L, 1L, testDto)).thenReturn(ResponseEntity.ok(testResponse));

            mockMvc.perform(put("/contributions/1")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testDto)))
            .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /contributions/{id}")
    class DeleteContribution {

        @Test
        @DisplayName("404 NOT FOUND if User doesn't have the Contribution to Delete")
        void returnsNoDeletedContributionIfNotFound() throws Exception {
            when(service.deleteOne(2L, 1L)).thenReturn(ResponseEntity.notFound().build());

            mockMvc.perform(delete("/contributions/2")).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("409 CONFLICT if Contribution Date has Past")
        void returnsConflictIfDatePast() throws Exception {
            when(service.deleteOne(1L, 1L)).thenReturn(ResponseEntity.status(409).build());

            mockMvc.perform(delete("/contributions/1")).andExpect(status().isConflict());
        }

        @Test
        @DisplayName("204 NO CONTENT if Contribution Date in Future")
        void returnsNoContentIfDateFuture() throws Exception {
            when(service.deleteOne(1L, 1L)).thenReturn(ResponseEntity.noContent().build());

            mockMvc.perform(delete("/contributions/1")).andExpect(status().isNoContent());
        }
    }
}
