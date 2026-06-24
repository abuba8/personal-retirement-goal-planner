package com.skillstorm.retirementplanner.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.skillstorm.retirementplanner.models.FundingSource;

public interface FundingSourceRepository extends CrudRepository<FundingSource, Long>, PagingAndSortingRepository<FundingSource, Long> {

}
