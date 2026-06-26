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

import com.skillstorm.retirementplanner.dtos.GoalDto;
import com.skillstorm.retirementplanner.mappers.GoalMapper;
import com.skillstorm.retirementplanner.models.Goal;
import com.skillstorm.retirementplanner.models.User;
import com.skillstorm.retirementplanner.repositories.GoalRepository;
import com.skillstorm.retirementplanner.repositories.UserRepository;

import jakarta.validation.Valid;

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
    private final GoalMapper goalMapper;
    private static final int PAGE_SIZE = 10;

    // parameterized constructor
    public GoalService(GoalRepository goalRepository, UserRepository userRepository, GoalMapper goalMapper) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
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
    public ResponseEntity<GoalDto> createGoal(Long userId, GoalDto goalDto){
        Optional<User> userObj = this.userRepository.findById(userId);
        if(userObj.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Goal obj = new Goal(null, userObj.get(), goalDto.name(), goalDto.targetRetirementAge(), goalDto.targetAmount(), goalDto.notes());
        Goal saved = this.goalRepository.save(obj);
        return ResponseEntity.status(201).body(this.goalMapper.toDto(saved));    }

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
    public ResponseEntity<List<GoalDto>> getAllGoals(Long userId) {
        List<GoalDto> goals = this.goalRepository.findByUserId(userId).stream()
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
    public ResponseEntity<Page<GoalDto>> getAllGoalsPaged(Long userId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").ascending());
        Page<GoalDto> goals = this.goalRepository.findByUserId(userId, pageable)
                .map(this.goalMapper::toDto);
        if (goals.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

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
    public ResponseEntity<GoalDto> getGoalById(Long userId, Long id){
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
    public ResponseEntity<GoalDto> deleteGoalById(Long userId, Long id){
        Optional<Goal> goalObj = this.goalRepository.findByIdAndUserId(id, userId);
        if(goalObj.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        this.goalRepository.delete(goalObj.get());
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<GoalDto> updateGoalById(Long userId, Long id, GoalDto goalDto){
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
