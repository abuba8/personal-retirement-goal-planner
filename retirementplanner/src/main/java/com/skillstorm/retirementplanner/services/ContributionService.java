package com.skillstorm.retirementplanner.services;

import com.skillstorm.retirementplanner.dtos.ContributionRequest;
import com.skillstorm.retirementplanner.repositories.FundingSourceRepository;
import com.skillstorm.retirementplanner.repositories.GoalRepository;
import com.skillstorm.retirementplanner.repositories.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.skillstorm.retirementplanner.dtos.ContributionResponse;
import com.skillstorm.retirementplanner.mappers.ContributionMapper;
import com.skillstorm.retirementplanner.models.Contribution;
import com.skillstorm.retirementplanner.models.FundingSource;
import com.skillstorm.retirementplanner.models.Goal;
import com.skillstorm.retirementplanner.models.User;
import com.skillstorm.retirementplanner.repositories.ContributionRepository;

@Service
public class ContributionService {
    
    private final FundingSourceRepository fundingRepo;
    private final GoalRepository goalRepo;
    private final ContributionRepository repo;
    private final UserRepository userRepo;
    private final ContributionMapper contributionMapper;
    private static final int PAGE_SIZE = 10;

    public ContributionService(FundingSourceRepository fundingRepo, GoalRepository goalRepo,
            ContributionRepository repo, UserRepository userRepo, ContributionMapper contributionMapper) {
        this.fundingRepo = fundingRepo;
        this.goalRepo = goalRepo;
        this.repo = repo;
        this.userRepo = userRepo;
        this.contributionMapper = contributionMapper;
    }

    /**
     * Get All Method takes in userId, goalId, sourceId and the page number to return
     * @param userId - used to find all Contributions by User conditionally
     *                  - can find with no UserId for later implementation of admin of some sort
     * @param goalId - used to find all Contributions associated with a given goal
     * @param sourceId - used to find all Contributions associated with a given funding source
     * @param page - the page number of results to return
     * @return - returns a Response Entity status wrapped around a page of Contributions
     */
    public ResponseEntity<Page<ContributionResponse>> getAll(Long userId, Long goalId, Long sourceId, int page) {
        Pageable pages = PageRequest.of(page, PAGE_SIZE, Sort.by("id"));
        if(userId == null) {
            return ResponseEntity.ok(this.repo.findAll(pages).map(this.contributionMapper::toDto));
        } else {
            if(goalId == null && sourceId == null) {
                return ResponseEntity.ok(this.repo.findByUserId(userId, pages).map(this.contributionMapper::toDto));
            } else if(goalId != null) {
                return ResponseEntity.ok(this.repo.findByGoalIdAndUserId(goalId, userId, pages).map(this.contributionMapper::toDto));
            } 
            return ResponseEntity.ok(this.repo.findByFundingSourceIdAndUserId(sourceId, userId, pages).map(this.contributionMapper::toDto));
        }
    }

    /**
     * Get One Method takes in a userId and contributionId
     * @param userId - determines if the given user is in the database
     * @param id - used to see if a given contribution is attached to the given user
     * @return - returns a contribution if the contribution and user are linked together, otherwise a 404
     */
    public ResponseEntity<ContributionResponse> getOne(Long userId, Long id) {

        Optional<Contribution> temp = this.repo.findByUserIdAndId(userId, id);
        if(temp.isPresent()) {
            return ResponseEntity.ok(this.contributionMapper.toDto(temp.get()));
        }
        return ResponseEntity.notFound().build();
        
    }

    /**
     * Create One Method takes in a contribution Dto, userId, sourceId, and goalId
     * @param dto - populates a contribution entity with an amount, date, category, and notes
     * @param userId - used to find the User the new contribution should belong to
     * @param sourceId - used to find the funding source the new contribution should be linked to
     * @param goalId - used to find the goal the new contribution should be linked to
     * @return - returns a Response Entity status wrapped around a Contribution object, or a 404 if the user, goal, or source isn't found
     */
    public ResponseEntity<ContributionResponse> createOne(ContributionRequest dto, Long userId, Long sourceId, Long goalId) {

        Optional<User> userObj = this.userRepo.findById(userId);
        if(userObj.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Goal> goalObj = this.goalRepo.findByIdAndUserId(goalId, userId);
        if(goalObj.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<FundingSource> sourceObj = fundingRepo.findByIdAndUserId(sourceId, userId);
        if(sourceObj.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Contribution contribution = this.repo.save(new Contribution(null, dto.amount(), dto.date(), dto.category(), dto.notes(),
                                                userObj.get(), goalObj.get(), sourceObj.get()));
        
        return ResponseEntity.status(201).body(this.contributionMapper.toDto(contribution));
    }

    /**
     * Update One Method takes in a contribution Id, userId, and a contribution dto
     * @param id - used to identify what contribution is being modified
     * @param userId - used to make sure the current User has access to the given contribution
     * @param dto - updates the contribution's amount, date, category, and notes when provided
     * @return - returns a Response Entity status code wrapped around a Contribution object, or a 404 if not found
     */
    public ResponseEntity<ContributionResponse> updateOne(Long id, Long userId, ContributionRequest dto) {

        Optional<Contribution> current = this.repo.findByUserIdAndId(userId, id);
        if(current.isPresent()) {
            Contribution temp = current.get();

            if(dto.amount() != null) temp.setAmount(dto.amount());
            if(dto.category() != null) temp.setCategory(dto.category());
            if(dto.date() != null) temp.setDate(dto.date());
            if(dto.notes() != null) temp.setNotes(dto.notes());

            Contribution updated = this.repo.save(temp);

            return ResponseEntity.ok().body(this.contributionMapper.toDto(updated));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete One Method takes in a contribution Id and a userId
     * @param id - used to identify what contribution is being deleted
     * @param userId - used to make sure the current User has access to the given contribution
     * @return - returns a no content response on successful delete, a 409 if the contribution's date has already
     *           passed preventing deletion of past contributions, or a 404 if not found
     */
    public ResponseEntity<Void> deleteOne(Long id, Long userId) {

        Optional<Contribution> temp = this.repo.findByUserIdAndId(userId, id);
        if(temp.isPresent()) {
            Contribution current = temp.get();
            if(current.getDate().isAfter(LocalDate.now())) {
                this.repo.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(409).build();
            }
        } else {
            return ResponseEntity.status(404).build();
        }
    }
}
