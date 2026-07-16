package com.skillstorm.retirementplanner.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.skillstorm.retirementplanner.dtos.GoalRequest;
import com.skillstorm.retirementplanner.dtos.GoalResponse;
import com.skillstorm.retirementplanner.mappers.GoalMapper;
import com.skillstorm.retirementplanner.models.Contribution;
import com.skillstorm.retirementplanner.models.Goal;
import com.skillstorm.retirementplanner.models.User;
import com.skillstorm.retirementplanner.repositories.ContributionRepository;
import com.skillstorm.retirementplanner.repositories.GoalRepository;
import com.skillstorm.retirementplanner.repositories.UserRepository;

@Service
public class GoalService {
    /**
     * Goal Class: All Business logic is implemented here for Goal Entity 
     * For each method an appropriate response entities is returned to the controller layer.
     * 
     * Methods:
     * POST/Create:
     * - createGoal(Long userId, GoalDto goalDto)
     * 
     * GET Methods:
     * - getAllGoals(Long userId)
     * - getGoalById(Long userId, Long id)
     * 
     * PUT/Update Methods;
     * - updateGoalById(Long userId, Long id, GoalDto goalDto) 
     * 
     * DELETE Methods:
     * - deleteGoalById(Long userId, Long id)
     * 
     */
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final ContributionRepository contributionRepository;
    private final GoalMapper goalMapper;
    private static final int PAGE_SIZE = 10;

    // parameterized constructor
    public GoalService(GoalRepository goalRepository, UserRepository userRepository, GoalMapper goalMapper, ContributionRepository contributionRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.contributionRepository = contributionRepository;
        this.goalMapper = goalMapper;
    }


    /**
     * createGoal:
     * POST request to create a goal for the current user.
     * 
     * args:
     * - Long userId: current user id
     * - GoalDto goalDto: data transfer object for creating new goal
     * 
     * return:
     * - 404: if object is empty
     * - 201: if object is successfully created
     */
    public ResponseEntity<GoalResponse> createGoal(Long userId, GoalRequest goalDto){
        Optional<User> userObj = this.userRepository.findById(userId);
        if(userObj.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Goal obj = new Goal(null, userObj.get(), goalDto.name(), goalDto.targetRetirementAge(), goalDto.targetAmount(), goalDto.notes());
        Goal saved = this.goalRepository.save(obj);
        return ResponseEntity.status(201).body(this.goalMapper.toDto(saved));    
    }

    // GET REQUESTS
    /**
     * getAllGoals (Non paginated):
     * GET request, fetches all the goals for the current user
     * Function invoked after logging in, so would return
     * 
     * args:
     * - Long userId: current user id
     * 
     * return:
     * - 200: if successfully found goals
     * Note: if there are no goals for the user, return empty list, no need to return 404
     */
    public ResponseEntity<List<GoalResponse>> getAllGoals(Long userId) {
        List<GoalResponse> goals = this.goalRepository.findByUserId(userId).stream()
            .sorted(Comparator.comparing(Goal::getId))
            .map(this.goalMapper::toDto)
            .toList();
        return ResponseEntity.ok(goals);
    }

    /**
     * getAllGoalsPaged (paginated):
     * GET request, fetches all the goals for the current user with pagination
     * 
     * args:
     * - Long userId: current user id
     * 
     * return:
     * - 200: if successfully found goals
     * Note: if there are no goals for the user, return empty list, no need to return 404
     */
    public ResponseEntity<Page<GoalResponse>> getAllGoalsPaged(Long userId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").ascending());
        Page<GoalResponse> goals = this.goalRepository.findByUserId(userId, pageable)
                .map(this.goalMapper::toDto);

        return ResponseEntity.ok(goals);
    }

    /**
     * getGoalById:
     * GET request, fetch a specific goal for the current user by its ID.
     * 
     * args: 
     * - Long userId: current user
     * - Long id: specific goal ID
     * 
     * return: 
     * - 404: if the goal object is empty or doesn't belong to the user
     * - 200: if successfully found it
     */
    public ResponseEntity<GoalResponse> getGoalById(Long userId, Long id){
        Optional<Goal> goalObj = this.goalRepository.findByIdAndUserId(id, userId);
        if(goalObj.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(this.goalMapper.toDto(goalObj.get()));
    }
 
    /**
     * deleteGoalById:
     * DELETE request, deletes a specific goal for the current user by its ID.
     * 
     * args: 
     * - Long userId: current user
     * - Long id: specific goal ID
     * 
     * return: 
     * - 404: if the goal object is empty or doesn't belong to the user
     * - 204: no content, if successfully deleted
     */
    public ResponseEntity<Void> deleteGoalById(Long userId, Long id){
        Optional<Goal> goalObj = this.goalRepository.findByIdAndUserId(id, userId);
        if(goalObj.isPresent()){
            Pageable pages = PageRequest.of(0, PAGE_SIZE, Sort.by("id"));
            Page<Contribution> contributionPage = this.contributionRepository.findByGoalIdAndUserId(id, userId, pages);
            List<Contribution> contributionList = contributionPage.getContent();

            if(contributionList.isEmpty()) {
                this.goalRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.status(409).build();
        }
        return ResponseEntity.status(404).build();
    }

    /**
     * updateGoalById:
     * UPDATE request, updates an existing goal's details by its ID for the current user.
     * 
     * args: 
     * - Long userId: current user
     * - Long id: specific goal ID
     * - GoalDto goalDto: data transfer object containing updated goal details
     * 
     * return: 
     * - 404: if the goal object is empty or doesn't belong to the user
     * - 200: if successfully updated
     */
    public ResponseEntity<GoalResponse> updateGoalById(Long userId, Long id, GoalRequest goalDto){
        Optional<Goal> goalObj = this.goalRepository.findByIdAndUserId(id, userId);
        if(goalObj.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Goal goal = goalObj.get();
        goal.setName(goalDto.name());
        goal.setTargetRetirementAge(goalDto.targetRetirementAge());
        goal.setTargetAmount(goalDto.targetAmount());
        goal.setNotes(goalDto.notes());
        Goal saved = this.goalRepository.save(goal);
        return ResponseEntity.ok(this.goalMapper.toDto(saved));
    }
}
