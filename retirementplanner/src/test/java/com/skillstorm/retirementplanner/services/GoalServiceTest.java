package com.skillstorm.retirementplanner.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.skillstorm.retirementplanner.dtos.GoalRequest;
import com.skillstorm.retirementplanner.dtos.GoalResponse;
import com.skillstorm.retirementplanner.mappers.GoalMapper;
import com.skillstorm.retirementplanner.models.Contribution;
import com.skillstorm.retirementplanner.models.Goal;
import com.skillstorm.retirementplanner.models.User;
import com.skillstorm.retirementplanner.repositories.ContributionRepository;
import com.skillstorm.retirementplanner.repositories.GoalRepository;
import com.skillstorm.retirementplanner.repositories.UserRepository;

/**
 * ExtendWith - JUnit annotation telling the class it needs the Mockito extension,
 * which initializes the @Mock objects before each test.
 */
@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    /**
     * GoalServiceTest Class:
     * Unit tests for GoalService's business logic, in isolation.
     * Repositories and the mapper are mocked, so no real DB runs.
     *
     * Tests:
     * createGoal():
     * - returnsCreatedWhenUserExists: 201 + saves
     * - returnsNotFoundWhenUserMissing: 404 + never saves
     *
     * getAllGoalsPaged():
     * - returnsPageOfGoals: 200 + page of goals
     * - returnsEmptyPageWhenNoGoals: 200 + empty page
     *
     * getGoalById():
     * - returnsGoalWhenOwned: 200 + goal
     * - returnsNotFoundWhenNotOwned: 404
     *
     * updateGoalById():
     * - returnsUpdatedGoalWhenOwned: 200 + saves
     * - returnsNotFoundWhenNotOwned: 404 + never saves
     *
     * deleteGoalById():
     * - returnsNoContentOnGoodDelete: 204 + deletes
     * - returnsNotFoundWhenNotOwned: 404 + never deletes
     */

    // fake repositories/mapper we fully control
    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContributionRepository contributionRepository;

    @Mock
    private GoalMapper goalMapper;

    // the real service under test, with the mocks above injected into it
    @InjectMocks
    private GoalService goalService;

    // Shared test fixtures, rebuilt fresh before every test.
    private User testUser;
    private Goal testGoal;
    private GoalRequest testRequest;
    private GoalResponse testResponse;

    // rebuilds the shared fixtures before each test so tests stay independent. 
    @BeforeEach
    void dataInit() {
        testUser = new User(1L, "molise", "m.olise@example.com", "hash");
        testGoal = new Goal(1L, testUser, "Early Retirement", 60,
                new BigDecimal("1000000.00"), "Main goal.");
        // What the client sends in (no id):
        testRequest = new GoalRequest("Early Retirement", 60,
                new BigDecimal("1000000.00"), "Main goal.");
        // What the mapper produces / we send out (with id):
        testResponse = new GoalResponse(1L, "Early Retirement", 60,
                new BigDecimal("1000000.00"), "Main goal.");
    }

    @Nested
    @DisplayName("createGoal()")
    class CreateGoal {
        /**
         * existing user: goal is saved and mapped.
         *
         * setup:
         * - findById(1): the user, save(goal) -> saved goal, toDto -> response
         *
         * assert:
         * - status 201 CREATED, body is the mapped response, save() was called
         */
        @Test
        @DisplayName("returns 201 CREATED with the goal when the user exists")
        void returnsCreatedWhenUserExists() {
            // user must exist for a goal to be created
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);
            when(goalMapper.toDto(testGoal)).thenReturn(testResponse);

            ResponseEntity<GoalResponse> results = goalService.createGoal(1L, testRequest);

            assertEquals(HttpStatus.CREATED, results.getStatusCode());
            assertEquals(testResponse, results.getBody());

            verify(goalRepository).save(any(Goal.class));
        }

        // no user then nothing is saved
        @Test
        @DisplayName("returns 404 NOT FOUND and never saves when the user is missing")
        void returnsNotFoundWhenUserMissing() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            ResponseEntity<GoalResponse> results = goalService.createGoal(99L, testRequest);

            assertEquals(HttpStatus.NOT_FOUND, results.getStatusCode());
            assertNull(results.getBody());

            verify(goalRepository, never()).save(any(Goal.class));
        }
    }

    @Nested
    @DisplayName("getAllGoalsPaged()")
    class GetAllGoalsPaged {
        /**
         * Returns the user's goals as a mapped page.
         *
         * setup:
         * - findByUserId(id, pageable): a page with one goal; toDto -> response
         *
         * assert:
         * - status 200, page has 1 item, the finder was called
         */
        @Test
        @DisplayName("returns 200 OK with a page of the user's goals")
        void returnsPageOfGoals() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Goal> goalPage = new PageImpl<>(List.of(testGoal), pageable, 1);

            when(goalRepository.findByUserId(anyLong(), any(Pageable.class))).thenReturn(goalPage);
            when(goalMapper.toDto(testGoal)).thenReturn(testResponse);

            ResponseEntity<Page<GoalResponse>> results = goalService.getAllGoalsPaged(1L, 0);

            assertEquals(HttpStatus.OK, results.getStatusCode());
            assertEquals(1, results.getBody().getContent().size());

            verify(goalRepository).findByUserId(anyLong(), any(Pageable.class));
        }

        // user with no goals still gets a normal 200 + empty page
        @Test
        @DisplayName("returns 200 OK with an empty page when the user has no goals")
        void returnsEmptyPageWhenNoGoals() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Goal> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(goalRepository.findByUserId(anyLong(), any(Pageable.class))).thenReturn(emptyPage);

            ResponseEntity<Page<GoalResponse>> results = goalService.getAllGoalsPaged(1L, 0);

            // a new user with no goals still gets a normal 200 + empty page
            assertEquals(HttpStatus.OK, results.getStatusCode());
            assertEquals(0, results.getBody().getContent().size());
        }
    }

    @Nested
    @DisplayName("getGoalById()")
    class GetGoalById {
        /**
         * The scoped finder returns the goal -> 200 with the mapped body.
         *
         * setup:
         * - findByIdAndUserId(1,1): the goal; toDto -> response
         *
         * assert:
         * - status 200, body is the response, finder was called
         */
        @Test
        @DisplayName("returns 200 OK with the goal when it belongs to the user")
        void returnsGoalWhenOwned() {
            when(goalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testGoal));
            when(goalMapper.toDto(testGoal)).thenReturn(testResponse);

            ResponseEntity<GoalResponse> results = goalService.getGoalById(1L, 1L);

            assertEquals(HttpStatus.OK, results.getStatusCode());
            assertEquals(testResponse, results.getBody());

            verify(goalRepository).findByIdAndUserId(1L, 1L);
        }

        // missing goal or a goal that doesn't belong to this user returns empty
        @Test
        @DisplayName("returns 404 NOT FOUND when the goal is not the user's")
        void returnsNotFoundWhenNotOwned() {
            // scoped finder returns empty -> not this user's goal (or doesn't exist)
            when(goalRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

            ResponseEntity<GoalResponse> results = goalService.getGoalById(2L, 1L);

            assertEquals(HttpStatus.NOT_FOUND, results.getStatusCode());
            assertNull(results.getBody());

            verify(goalRepository).findByIdAndUserId(1L, 2L);
        }
    }

    @Nested
    @DisplayName("updateGoalById()")
    class UpdateGoalById {
        /**
         * Owned goal: fields updated, saved, and mapped back.
         *
         * setup:
         * - findByIdAndUserId(1,1): goal; save -> goal; toDto -> response
         *
         * assert:
         * - status 200, body is the response, finder + save were called
         */
        @Test
        @DisplayName("returns 200 OK with the updated goal when it belongs to the user")
        void returnsUpdatedGoalWhenOwned() {
            when(goalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testGoal));
            when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);
            when(goalMapper.toDto(testGoal)).thenReturn(testResponse);

            ResponseEntity<GoalResponse> results = goalService.updateGoalById(1L, 1L, testRequest);

            assertEquals(HttpStatus.OK, results.getStatusCode());
            assertEquals(testResponse, results.getBody());

            verify(goalRepository).findByIdAndUserId(1L, 1L);
            verify(goalRepository).save(any(Goal.class));
        }

        // not the user's goal -> 404 and nothing's saved
        @Test
        @DisplayName("returns 404 NOT FOUND and never saves when the goal is not the user's")
        void returnsNotFoundWhenNotOwned() {
            when(goalRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

            ResponseEntity<GoalResponse> results = goalService.updateGoalById(2L, 1L, testRequest);

            assertEquals(HttpStatus.NOT_FOUND, results.getStatusCode());
            assertNull(results.getBody());

            verify(goalRepository).findByIdAndUserId(1L, 2L);
            verify(goalRepository, never()).save(any(Goal.class));
        }
    }

    @Nested
    @DisplayName("deleteGoalById()")
    class DeleteGoalById {
        /**
         * Owned goal: deleted, 204 with no body.
         *
         * setup:
         * - findByIdAndUserId(1,1): the goal
         *
         * assert:
         * - status 204, null body, finder + delete were called
         */
        @Test
        @DisplayName("returns 204 NO CONTENT and deletes when the goal belongs to the user")
        void returnsNoContentOnGoodDelete() {
            when(goalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testGoal));

            Page<Contribution> emptyContributionPage = new PageImpl<>(List.of());
            when(contributionRepository.findByGoalIdAndUserId(eq(1L), eq(1L), any(Pageable.class)))
                    .thenReturn(emptyContributionPage);

            ResponseEntity<Void> results = goalService.deleteGoalById(1L, 1L);

            assertEquals(HttpStatus.NO_CONTENT, results.getStatusCode());
            assertNull(results.getBody());

            verify(goalRepository).findByIdAndUserId(1L, 1L);
            verify(goalRepository).deleteById(1L);
        }

        // not the user's goal -> 404 and nothing is deleted
        @Test
        @DisplayName("returns 404 NOT FOUND and never deletes when the goal is not the user's")
        void returnsNotFoundWhenNotOwned() {
            when(goalRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

            ResponseEntity<Void> results = goalService.deleteGoalById(2L, 1L);

            assertEquals(HttpStatus.NOT_FOUND, results.getStatusCode());
            assertNull(results.getBody());

            verify(goalRepository).findByIdAndUserId(1L, 2L);
            verify(goalRepository, never()).delete(any(Goal.class));
        }

        @Test
        @DisplayName("returns 409 CONFLICT and never deletes when the goal is connected to contributions")
        void returnsConflictWhenContributionsFound() {
            when(goalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testGoal));

            // one contribution is enough to make the list non-empty and trigger the conflict
            Page<Contribution> contributionPage = new PageImpl<>(List.of(new Contribution()));
            when(contributionRepository.findByGoalIdAndUserId(eq(1L), eq(1L), any(Pageable.class)))
                    .thenReturn(contributionPage);

            ResponseEntity<Void> results = goalService.deleteGoalById(1L, 1L);

            assertEquals(HttpStatus.CONFLICT, results.getStatusCode());
            assertNull(results.getBody());

            verify(goalRepository, never()).deleteById(anyLong());
        }
    }
}