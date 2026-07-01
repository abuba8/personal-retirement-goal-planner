package com.skillstorm.retirementplanner.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.skillstorm.retirementplanner.models.Contribution;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    Page<Contribution> findByUserId(Long userId, Pageable pageable);
    Page<Contribution> findByGoalIdAndUserId(Long goalId, Long userId, Pageable pageable);
    Page<Contribution> findByFundingSourceIdAndUserId(Long sourceId, Long userId, Pageable pageable);
    Optional<Contribution> findByUserIdAndId(Long userId, Long id);

}
