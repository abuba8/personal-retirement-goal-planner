package com.skillstorm.retirementplanner.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.skillstorm.retirementplanner.dtos.FundingSourceDto;
import com.skillstorm.retirementplanner.models.FundingSource;
import com.skillstorm.retirementplanner.models.User;
import com.skillstorm.retirementplanner.models.enums.SourceType;
import com.skillstorm.retirementplanner.services.FundingSourceService;

@WebMvcTest(FundingSourceController.class)
@DisplayName("Funding Source Controller - Web Layer Tests")
public class FundingSourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FundingSourceService service;

    private FundingSource testSource;
    private Pageable testPage;
    private List<FundingSource> sources;
    private Page<FundingSource> sourcePage;
    private FundingSourceDto testDto;

    @BeforeEach
    void dataInit() {
        testSource = new FundingSource(1L, "Work 401k", "Fidelity",  "Primary employer retirement account.", 
                                        new User(), SourceType.ROTH_IRA);
        
        testPage = PageRequest.of(0, 6);
        sources = List.of(testSource, testSource, testSource, testSource, testSource, testSource);
        sourcePage = new PageImpl<>(sources,testPage, sources.size());
        testDto = new FundingSourceDto("Work 401k", "Fidelity",  
        "Primary employer retirement account.", SourceType.ROTH_IRA);
    }

    @Nested
    @DisplayName("GET /sources")
    class GetAllSources {

        @Test
        @DisplayName("200 OK with a Page of all Funding Sources with no User")
        void returnsAllSources() throws Exception {
            when(service.getAll(null, 0)).thenReturn(ResponseEntity.ok(sourcePage));

            mockMvc.perform(get("/sources")).andExpect(status().isOk());
        }

        @Test
        @DisplayName("200 OK with a Page of all Funding Sources of a given User")
        void returnsAllUsersSources() throws Exception {
            when(service.getAll(1L, 0)).thenReturn(ResponseEntity.ok(sourcePage));

            mockMvc.perform(get("/sources").param("userId", "1")).andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /sources/{id}")
    class GetOneSource {
        
        @Test
        @DisplayName("404 NOT FOUND when User doesn't have the given Source")
        void returnsNoSourceIfNotFound() throws Exception {
            when(service.getOne(1L, 2L)).thenReturn(ResponseEntity.notFound().build());

            mockMvc.perform(get("/sources/2").param("userId", "1")).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("200 OK if User has the matching Source")
        void returnsOneSourceIfFound() throws Exception {
            when(service.getOne(1L, 1L)).thenReturn(ResponseEntity.ok(testSource));

            mockMvc.perform(get("/sources/1").param("userId", "1")).andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("POST /sources")
    class CreateSource {

        @Test
        @DisplayName("201 CREATED if User is Found")
        void returnsCreatedSourceIfUserFound() throws Exception {
            when(service.createOne(1L, testDto)).thenReturn(ResponseEntity.status(201).body(testSource));

            mockMvc.perform(post("/sources").param("userId", "1")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testDto)))
            .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("PUT /sources/{id}")
    class UpdateSources {
    
        @Test
        @DisplayName("404 NOT FOUND if User doesn't have the given Source to Update")
        void returnsNoUpdatedSourceIfNotFound() throws Exception {
            when(service.updateOne(2L, 1L, testDto)).thenReturn(ResponseEntity.notFound().build());

            mockMvc.perform(put("/sources/2").param("userId", "1")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testDto)))
            .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("200 OK if Source is Found for Updating")
        void returnsUpdatedSourceIfFound() throws Exception {
            when(service.updateOne(1L, 1L, testDto)).thenReturn(ResponseEntity.ok(testSource));

            mockMvc.perform(put("/sources/1").param("userId", "1")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testDto)))
            .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /sources/{id}")
    class DeleteSource {

        @Test
        @DisplayName("404 NOT FOUND if User doesn't have the Source to Delete")
        void returnsNoDeletedSourceIfNotFound() throws Exception {
            when(service.deleteOne(2L, 1L)).thenReturn(ResponseEntity.notFound().build());

            mockMvc.perform(delete("/sources/2").param("userId", "1")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testDto))).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("409 CONFLICT if Source has Contributions")
        void returnsConflictIfContributionsFound() throws Exception {
            when(service.deleteOne(1L, 1L)).thenReturn(ResponseEntity.status(409).build());

            mockMvc.perform(delete("/sources/1").param("userId", "1")).andExpect(status().isConflict());
        }

        @Test
        @DisplayName("204 NO CONTENT if Source has no Contributions")
        void returnsNoContentIfContributionsEmpty() throws Exception {
            when(service.deleteOne(1L, 1L)).thenReturn(ResponseEntity.noContent().build());

            mockMvc.perform(delete("/sources/1").param("userId", "1")).andExpect(status().isNoContent());
        }
    }
    
}
