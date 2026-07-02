package com.skillstorm.retirementplanner.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.skillstorm.retirementplanner.models.Goal;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    /**
     * GoalRepository: functioning as a DAO that handles the database interactions for Goal Model
     * 
     * Custom Methods:
     * - findByUserId(Long userId): Get all goals for the current user by id
     * - findByIdAndUserId(Long id, Long userId): Get a specific goal for the user by given id
     * - existsByIdAndUserId(Long id, Long userId): find if the goal id exists for the current user, and returns a boolean
     */
    List<Goal> findByUserId(Long userId); // internal use
    Page<Goal> findByUserId(Long userId, Pageable pageable); // paginated version
    Optional<Goal> findByIdAndUserId(Long id, Long userId);
    boolean existsByIdAndUserId(Long id, Long userId);
}