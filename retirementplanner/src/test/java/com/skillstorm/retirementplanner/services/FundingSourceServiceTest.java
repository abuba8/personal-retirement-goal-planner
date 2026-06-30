package com.skillstorm.retirementplanner.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.skillstorm.retirementplanner.dtos.FundingSourceRequest;
import com.skillstorm.retirementplanner.dtos.FundingSourceResponse;
import com.skillstorm.retirementplanner.mappers.FundingSourceMapper;
import com.skillstorm.retirementplanner.models.Contribution;
import com.skillstorm.retirementplanner.models.FundingSource;
import com.skillstorm.retirementplanner.models.Goal;
import com.skillstorm.retirementplanner.models.User;
import com.skillstorm.retirementplanner.models.enums.ContributionCategory;
import com.skillstorm.retirementplanner.models.enums.SourceType;
import com.skillstorm.retirementplanner.repositories.ContributionsRepository;
import com.skillstorm.retirementplanner.repositories.FundingSourceRepository;
import com.skillstorm.retirementplanner.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class FundingSourceServiceTest {
    
    @Mock
    private FundingSourceRepository fundingRepo;

    @Mock
    private ContributionsRepository contributionRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private FundingSourceMapper sourceMapper;

    @InjectMocks
    private FundingSourceService fundingService;

    private FundingSource testSource;
    private FundingSourceResponse testResponse;
    private Contribution testContribution;
    private Pageable testPage;
    private List<FundingSource> sources;
    private List<Contribution> contributions;
    private Page<FundingSource> sourcePage;
    private Page<Contribution> contributionPage;
    private Page<Contribution> emptyPage;
    private FundingSourceRequest testDto;

    @BeforeEach
    void dataInit() {
        testSource = new FundingSource(1L, "Work 401k", "Fidelity",  "Primary employer retirement account.", 
                                        new User(), SourceType.ROTH_IRA);
        testContribution = new Contribution(1L, new BigDecimal("500.00"), LocalDate.now().plusDays(1), ContributionCategory.EMPLOYEE_SALARY_DEFERRAL, "January paycheck contribution.", 
                            new User(), new Goal(), new FundingSource());
        
        testResponse = this.sourceMapper.toDto(testSource);
        testPage = PageRequest.of(0, 10, Sort.by("id"));
        sources = List.of(testSource, testSource, testSource, testSource, testSource, testSource,
            testSource, testSource, testSource, testSource
        );
        contributions = List.of(testContribution, testContribution, testContribution, testContribution, testContribution, testContribution,
            testContribution, testContribution, testContribution, testContribution
        );
        sourcePage = new PageImpl<>(sources,testPage, sources.size());
        contributionPage = new PageImpl<>(contributions, testPage, contributions.size());
        emptyPage = new PageImpl<>(List.of(), testPage, 0);
        testDto = new FundingSourceRequest("Work 401k", "Fidelity",  
        "Primary employer retirement account.", SourceType.ROTH_IRA);
    }

    @Nested
    @DisplayName("getAllSources()")
    class GetSources {
        @Test
        @DisplayName("findAllNoParams")
        void returnsAllSourcesWithNoUserId() {
            when(fundingRepo.findAll(testPage)).thenReturn(sourcePage);

            ResponseEntity<Page<FundingSourceResponse>> results = fundingService.getAll(null, 0);

            assertEquals(HttpStatus.OK, results.getStatusCode());
            assertEquals(10, results.getBody().getContent().size());
            assertEquals(testResponse, results.getBody().getContent().get(0));
            
            verify(fundingRepo).findAll(testPage);
            verify(fundingRepo, never()).findByUserId(anyLong(), any(Pageable.class));
        }

        @Test
        @DisplayName("findAllWithParams")
        void returnsAllSourcesWithUserId() {
            when(fundingRepo.findByUserId(1L, testPage))
            .thenReturn(sourcePage);

            ResponseEntity<Page<FundingSourceResponse>> results = fundingService.getAll(1L, 0);

            assertEquals(HttpStatus.OK, results.getStatusCode());
            assertEquals(10, results.getBody().getContent().size());
            assertEquals(testResponse, results.getBody().getContent().get(0));
            
            verify(fundingRepo).findByUserId(1L, testPage);
            verify(fundingRepo, never()).findAll(testPage);
        }
    }

    @Nested
    @DisplayName("getOneSource")
    class GetOneSource {
        @Test
        @DisplayName("getOneSourceWithParams")
        void returnsOneSourceWithUserIdandSourceId() {
            when(fundingRepo.findByIdAndUserId(1L, 1L))
            .thenReturn(Optional.of(testSource));

            ResponseEntity<FundingSourceResponse> results = fundingService.getOne(1L, 1L);

            assertEquals(HttpStatus.OK, results.getStatusCode());
            assertEquals(testResponse, results.getBody());
            
            verify(fundingRepo).findByIdAndUserId(1L, 1L);
        }

        @Test
        @DisplayName("getNoSourceWithParms")
        void returnsNoSourceWithUserIdandSourceId() {
            when(fundingRepo.findByIdAndUserId(1L, 2L))
            .thenReturn(Optional.empty());

            ResponseEntity<FundingSourceResponse> results = fundingService.getOne(1L, 2L);

            assertEquals(HttpStatus.NOT_FOUND, results.getStatusCode());
            assertNull(results.getBody());

            verify(fundingRepo).findByIdAndUserId(1L, 2L);
        }
    }

    @Nested
    @DisplayName("createOneFundingSource")
    class CreateOneSource {
        @Test
        @DisplayName("createOneWithParams")
        void returnsNewSourceWithSourceDto() {
            when(userRepo.findById(1L)).thenReturn(Optional.of(new User()));

            when(fundingRepo.save(any(FundingSource.class)))
            .thenReturn(testSource);

            ResponseEntity<FundingSourceResponse> results = fundingService.createOne(1L, testDto);

            assertEquals(HttpStatus.CREATED, results.getStatusCode());
            assertEquals(testResponse, results.getBody());

            verify(userRepo).findById(1L);
            verify(fundingRepo).save(any(FundingSource.class));
        }

        @Test
        @DisplayName("createOneWithWrongUser")
        void returnsNotFoundIfUserWrong() {
            when(userRepo.findById(2L)).thenReturn(Optional.empty());

            ResponseEntity<FundingSourceResponse> results = fundingService.createOne(2L, testDto);

            assertEquals(HttpStatus.NOT_FOUND, results.getStatusCode());
            assertNull(results.getBody());

            verify(userRepo).findById(2L);
            verify(fundingRepo, never()).save(any(FundingSource.class));
        }
    }

    @Nested
    @DisplayName("updateSource")
    class UpdateSource {
        @Test
        @DisplayName("updateWithMatchingParam")
        void returnsNewUpdatedSourceIfMatching() {
            when(fundingRepo.findByIdAndUserId(1L, 1L))
            .thenReturn(Optional.of(testSource));

            when(fundingRepo.save(any(FundingSource.class)))
            .thenReturn(testSource);

            ResponseEntity<FundingSourceResponse> results = fundingService.updateOne(1L, 1L, testDto);

            assertEquals(HttpStatus.OK, results.getStatusCode());
            assertEquals(testResponse, results.getBody());

            verify(fundingRepo).findByIdAndUserId(1L, 1L);
            verify(fundingRepo).save(any(FundingSource.class));
        }

        @Test
        @DisplayName("noUpdateWithBadSourceId")
        void returnsNoUpdatedSource() {
            when(fundingRepo.findByIdAndUserId(2L, 2L))
            .thenReturn(Optional.empty());

            ResponseEntity<FundingSourceResponse> results = fundingService.updateOne(2L, 2L, testDto);

            assertEquals(HttpStatus.NOT_FOUND, results.getStatusCode());
            assertNull(results.getBody());

            verify(fundingRepo).findByIdAndUserId(2L, 2L);
            verify(fundingRepo, never()).save(any(FundingSource.class));
        }
    }

    @Nested
    @DisplayName("deleteSource")
    class DeleteSource {
        @Test
        @DisplayName("deleteSourceWithMatchingParams")
        void returnsNoContentOnGoodDelete() {
            when(fundingRepo.findByIdAndUserId(1L, 1L))
            .thenReturn(Optional.of(testSource));

            when(contributionRepo.findByFundingSourceIdAndUserId(1L, 1L, testPage))
            .thenReturn(emptyPage);

            ResponseEntity<Void> results = fundingService.deleteOne(1L, 1L);

            assertEquals(HttpStatus.NO_CONTENT, results.getStatusCode());
            assertNull(results.getBody());

            verify(fundingRepo).findByIdAndUserId(1L, 1L);
            verify(contributionRepo).findByFundingSourceIdAndUserId(1L, 1L, testPage);
            verify(fundingRepo).deleteById(1L);
        }

        @Test
        @DisplayName("deleteSourceBlockedIfContributionFound")
        void returnsConflictIfContributionFound() {
            when(fundingRepo.findByIdAndUserId(1L, 1L))
            .thenReturn(Optional.of(testSource));

            when(contributionRepo.findByFundingSourceIdAndUserId(1L, 1L, testPage))
            .thenReturn(contributionPage);

            ResponseEntity<Void> results = fundingService.deleteOne(1L, 1L);

            assertEquals(HttpStatus.CONFLICT, results.getStatusCode());
            assertNull(results.getBody());

            verify(fundingRepo).findByIdAndUserId(1L, 1L);
            verify(contributionRepo).findByFundingSourceIdAndUserId(1L, 1L, testPage);
            verify(fundingRepo, never()).deleteById(1L);
        }

        @Test
        @DisplayName("failDeleteIfWrongUser")
        void returnsMethodNotAllowedIfWrongUser() {
            when(fundingRepo.findByIdAndUserId(1L, 2L))
            .thenReturn(Optional.empty());

            ResponseEntity<Void> results = fundingService.deleteOne(1L, 2L);

            assertEquals(HttpStatus.NOT_FOUND, results.getStatusCode());
            assertNull(results.getBody());

            verify(fundingRepo).findByIdAndUserId(1L, 2L);
            verify(contributionRepo, never()).findByFundingSourceIdAndUserId(anyLong(), anyLong(), any(Pageable.class));
            verify(fundingRepo, never()).deleteById(anyLong());
        }
    }
}
