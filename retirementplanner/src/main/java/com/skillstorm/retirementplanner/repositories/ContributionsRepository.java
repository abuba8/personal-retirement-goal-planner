package com.skillstorm.retirementplanner.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.skillstorm.retirementplanner.models.Contribution;

public interface ContributionsRepository extends JpaRepository<Contribution, Long> {

    Page<Contribution> findByGoalId(Long goalId, Pageable pageable);
    Page<Contribution> findByUserId(Long userId, Pageable pageable);
    Page<Contribution> findBySourceId(Long sourceId, Pageable pageable);

}
