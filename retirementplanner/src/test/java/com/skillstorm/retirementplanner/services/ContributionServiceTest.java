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
import java.time.Month;
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

import com.skillstorm.retirementplanner.dtos.ContributionRequest;
import com.skillstorm.retirementplanner.dtos.ContributionResponse;
import com.skillstorm.retirementplanner.mappers.ContributionMapper;
import com.skillstorm.retirementplanner.models.Contribution;
import com.skillstorm.retirementplanner.models.FundingSource;
import com.skillstorm.retirementplanner.models.Goal;
import com.skillstorm.retirementplanner.models.User;
import com.skillstorm.retirementplanner.models.enums.ContributionCategory;
import com.skillstorm.retirementplanner.repositories.ContributionRepository;
import com.skillstorm.retirementplanner.repositories.FundingSourceRepository;
import com.skillstorm.retirementplanner.repositories.GoalRepository;
import com.skillstorm.retirementplanner.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ContributionServiceTest {

    @Mock
    private ContributionRepository repo;

    @Mock
    private FundingSourceRepository fundingRepo;

    @Mock
    private GoalRepository goalRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private ContributionMapper contributionMapper;

    @InjectMocks
    private ContributionService contributionService;

    private Contribution testContribution;
    private Contribution testContributionPast;
    private ContributionResponse testResponse;
    private Pageable testPage;
    private List<Contribution> contributions;
    private Page<Contribution> contributionPage;
    private ContributionRequest testDto;

    @BeforeEach
    void dataInit() {
        testContribution = new Contribution(1L, new BigDecimal("500.00"), LocalDate.now().plusDays(1), ContributionCategory.EMPLOYEE_SALARY_DEFERRAL, "January paycheck contribution.", 
                            new User(), new Goal(), new FundingSource());
        testContributionPast = new Contribution(4L, new BigDecimal("500.00"), LocalDate.now().minusDays(1), ContributionCategory.EMPLOYEE_SALARY_DEFERRAL, "January paycheck contribution.", 
                            new User(), new Goal(), new FundingSource());

        testResponse = this.contributionMapper.toDto(testContribution);
        testPage = PageRequest.of(0, 10, Sort.by("id"));
        contributions = List.of(testContribution, testContribution, testContribution, testContributionPast, testContribution, testContribution,
            testContribution, testContribution, testContribution, testContribution
        );
        contributionPage = new PageImpl<>(contributions, testPage, contributions.size());
        testDto = new ContributionRequest(new BigDecimal("500.00"), LocalDate.of(2026, Month.JANUARY, 15), ContributionCategory.EMPLOYEE_SALARY_DEFERRAL, "January paycheck contribution.");
    }

    @Nested
    @DisplayName("getAllContributions")
    class GetContributions {
        @Test
        @DisplayName("findAllNoParams")
        void returnsAllContributionsWithNoUserId() {
            when(repo.findAll(testPage)).thenReturn(contributionPage);

            ResponseEntity<Page<ContributionResponse>> results = contributionService.getAll(null, null, null, 0);

            assertEquals(HttpStatus.OK, results.getStatusCode());
            assertEquals(10, results.getBody().getContent().size());
            assertEquals(testResponse, results.getBody().getContent().get(0));

            verify(repo).findAll(testPage);
            verify(repo, never()).findByUserId(anyLong(), any(Pageable.class));
            verify(repo, never()).findByGoalIdAndUserId(anyLong(), anyLong(), any(Pageable.class));
            verify(repo, never()).findByFundingSourceIdAndUserId(anyLong(), anyLong(), any(Pageable.class));
        }

        @Test
        @DisplayName("FindAllWithUserId")
        void returnsAllContributionsForGivenUser() {
            when(repo.findByUserId(1L, testPage)).thenReturn(contributionPage);

            ResponseEntity<Page<ContributionResponse>> results = contributionService.getAll(1L, null, null, 0);

            assertEquals(HttpStatus.OK, results.getStatusCode());
            assertEquals(10, results.getBody().getContent().size());
            assertEquals(testResponse, results.getBody().getContent().get(0));

            verify(repo).findByUserId(1L, testPage);
            verify(repo, never()).findAll(testPage);
            verify(repo, never()).findByGoalIdAndUserId(anyLong(), anyLong(), any(Pageable.class));
            verify(repo, never()).findByFundingSourceIdAndUserId(anyLong(), anyLong(), any(Pageable.class));
        }

        @Test
        @DisplayName("FindAllWithGoalId")
        void returnsAllContributionsByGoalId() {
            when(repo.findByGoalIdAndUserId(1L, 1L, testPage)).thenReturn(contributionPage);

            ResponseEntity<Page<ContributionResponse>> results = contributionService.getAll(1L, 1L, null, 0);

            assertEquals(HttpStatus.OK, results.getStatusCode());
            assertEquals(10, results.getBody().getContent().size());
            assertEquals(testResponse, results.getBody().getContent().get(0));

            verify(repo).findByGoalIdAndUserId(1L, 1L, testPage);
            verify(repo, never()).findAll(testPage);
            verify(repo, never()).findByUserId(anyLong(), any(Pageable.class));
            verify(repo, never()).findByFundingSourceIdAndUserId(anyLong(), anyLong(), any(Pageable.class));
        }

        @Test
        @DisplayName("FindAllWithSourceId")
        void returnsAllContributionsBySourceId() {
            when(repo.findByFundingSourceIdAndUserId(1L, 1L, testPage)).thenReturn(contributionPage);

            ResponseEntity<Page<ContributionResponse>> results = contributionService.getAll(1L, null, 1L, 0);

            assertEquals(HttpStatus.OK, results.getStatusCode());
            assertEquals(10, results.getBody().getContent().size());
            assertEquals(testResponse, results.getBody().getContent().get(0));

            verify(repo).findByFundingSourceIdAndUserId(1L, 1L, testPage);
            verify(repo, never()).findAll(testPage);
            verify(repo, never()).findByUserId(anyLong(), any(Pageable.class));
            verify(repo, never()).findByGoalIdAndUserId(anyLong(), anyLong(), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("getOneContribution")
    class GetOneContribution {
        @Test
        @DisplayName("getOneContributionWithParams")
        void returnsOneContributionWithParams() {
            when(repo.findByUserIdAndId(1L, 1L))
            .thenReturn(Optional.of(testContribution));

            ResponseEntity<ContributionResponse>  results = contributionService.getOne(1L, 1L);

            assertEquals(HttpStatus.OK, results.getStatusCode());
            assertEquals(testResponse, results.getBody());

            verify(repo).findByUserIdAndId(1L, 1L);
        }

        @Test
        @DisplayName("getNoContributionWithParams")
        void returnsNoContributionWithParams() {
            when(repo.findByUserIdAndId(2L, 2L))
            .thenReturn(Optional.empty());

            ResponseEntity<ContributionResponse>  results = contributionService.getOne(2L, 2L);

            assertEquals(HttpStatus.NOT_FOUND, results.getStatusCode());
            assertNull(results.getBody());

            verify(repo).findByUserIdAndId(2L, 2L);
        }
    }

    @Nested
    @DisplayName("createOneContribution")
    class CreateOneContribution {
        @Test
        @DisplayName("createOneWithCorrectSource")
        void returnsNewContributionWithSource() {
            when(userRepo.findById(1L)).thenReturn(Optional.of(new User()));

            when(goalRepo.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(new Goal()));

            when(fundingRepo.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(new FundingSource()));

            when(repo.save(any(Contribution.class))).thenReturn(testContribution);

            ResponseEntity<ContributionResponse> results = contributionService.createOne(testDto, 1L, 1L, 1L);

            assertEquals(HttpStatus.CREATED, results.getStatusCode());
            assertEquals(testResponse, results.getBody());

            verify(userRepo).findById(1L);
            verify(goalRepo).findByIdAndUserId(1L, 1L);
            verify(fundingRepo).findByIdAndUserId(1L, 1L);
            verify(repo).save(any(Contribution.class));
        }

        @Test
        @DisplayName("createOneWithBadUser")
        void returnsNoContributionIfBadUser() {
            when(userRepo.findById(2L)).thenReturn(Optional.empty());

            ResponseEntity<ContributionResponse> results = contributionService.createOne(testDto, 2L, 1L, 1L);

            assertEquals(HttpStatus.NOT_FOUND, results.getStatusCode());
            assertNull(results.getBody());

            verify(userRepo).findById(2L);
            verify(goalRepo, never()).findByIdAndUserId(anyLong(), anyLong());
            verify(fundingRepo, never()).findByIdAndUserId(anyLong(), anyLong());
            verify(repo, never()).save(any(Contribution.class));
        }

        @Test
        @DisplayName("createOneWithBadGoal")
        void returnsNoContributionIfBadGoal() {
            when(userRepo.findById(1L)).thenReturn(Optional.of(new User()));

            when(goalRepo.findByIdAndUserId(2L, 1L)).thenReturn(Optional.empty());

            ResponseEntity<ContributionResponse> results = contributionService.createOne(testDto, 1L, anyLong(), 2L);

            assertEquals(HttpStatus.NOT_FOUND, results.getStatusCode());
            assertNull(results.getBody());

            verify(userRepo).findById(1L);
            verify(goalRepo).findByIdAndUserId(2L, 1L);
            verify(fundingRepo, never()).findByIdAndUserId(anyLong(), anyLong());
            verify(repo, never()).save(any(Contribution.class));
        }

        @Test
        @DisplayName("createOneWithBadSource")
        void returnsNoContributionIfBadSource() {
            when(userRepo.findById(1L)).thenReturn(Optional.of(new User()));

            when(goalRepo.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(new Goal()));

            when(fundingRepo.findByIdAndUserId(2L, 1L)).thenReturn(Optional.empty());

            ResponseEntity<ContributionResponse> results = contributionService.createOne(testDto, 1L, 2L, 1L);

            assertEquals(HttpStatus.NOT_FOUND, results.getStatusCode());
            assertNull(results.getBody());

            verify(userRepo).findById(1L);
            verify(goalRepo).findByIdAndUserId(1L, 1L);
            verify(fundingRepo).findByIdAndUserId(2L, 1L);
            verify(repo, never()).save(any(Contribution.class));
        }
    }

    @Nested
    @DisplayName("updateOneContribution")
    class UpdateOneContribution {
        @Test
        @DisplayName("updateOneNotFound")
        void returnsNoContributionIfNotFound() {
            when(repo.findByUserIdAndId(1L, 2L)).thenReturn(Optional.empty());

            ResponseEntity<ContributionResponse> results = contributionService.updateOne(2L, 1L, testDto);

            assertEquals(HttpStatus.NOT_FOUND, results.getStatusCode());
            assertNull(results.getBody());

            verify(repo).findByUserIdAndId(1L, 2L);
            verify(repo, never()).save(any(Contribution.class));
        }

        @Test
        @DisplayName("updateOneFound")
        void returnsUpdatedContributionIfFound() {
            when(repo.findByUserIdAndId(1L, 1L)).thenReturn(Optional.of(testContribution));

            when(repo.save(any(Contribution.class))).thenReturn(testContribution);

            ResponseEntity<ContributionResponse> results = contributionService.updateOne(1L, 1L, testDto);

            assertEquals(HttpStatus.OK, results.getStatusCode());
            assertEquals(testResponse, results.getBody());

            verify(repo).findByUserIdAndId(1L, 1L);
            verify(repo).save(any(Contribution.class));
        }
    }

    @Nested
    @DisplayName("deleteOneContribution")
    class DeleteOneContribution {
        @Test
        @DisplayName("deleteOneIfNotFound")
        void returnsNotFoundIfNotFound() {
            when(repo.findByUserIdAndId(1L, 2L)).thenReturn(Optional.empty());

            ResponseEntity<Void> results = contributionService.deleteOne(2L, 1L);

            assertEquals(HttpStatus.NOT_FOUND, results.getStatusCode());
            assertNull(results.getBody());

            verify(repo).findByUserIdAndId(1L, 2L);
            verify(repo, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("deleteOneBlockedIfDatePast")
        void returnsConflictIfDatePast() {
            when(repo.findByUserIdAndId(1L, 4L)).thenReturn(Optional.of(testContributionPast));

            ResponseEntity<Void> results = contributionService.deleteOne(4L, 1L);

            assertEquals(HttpStatus.CONFLICT, results.getStatusCode());
            assertNull(results.getBody());

            verify(repo).findByUserIdAndId(1L, 4L);
            verify(repo, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("deleteOneIfDateFuture")
        void returnsNoContentIfDateFuture() {
            when(repo.findByUserIdAndId(1L, 1L)).thenReturn(Optional.of(testContribution));

            ResponseEntity<Void> results = contributionService.deleteOne(1L, 1L);

            assertEquals(HttpStatus.NO_CONTENT, results.getStatusCode());
            assertNull(results.getBody());

            verify(repo).findByUserIdAndId(1L, 1L);
            verify(repo).deleteById(1L);
        }
    }
    
}
