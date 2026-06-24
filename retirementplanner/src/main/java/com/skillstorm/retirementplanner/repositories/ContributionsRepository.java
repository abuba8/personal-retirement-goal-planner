package com.skillstorm.retirementplanner.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.skillstorm.retirementplanner.models.Contribution;

public interface ContributionsRepository extends CrudRepository<Contribution, Long>, PagingAndSortingRepository<Contribution, Long> {

}
