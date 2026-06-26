package com.skillstorm.retirementplanner.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.skillstorm.retirementplanner.models.FundingSource;

public interface FundingSourceRepository extends JpaRepository<FundingSource, Long> {

    Page<FundingSource> findByUserId(Long userId, Pageable pageable);
    Optional<FundingSource> findOneByUserId(Long userId, Long sourceId);

}
