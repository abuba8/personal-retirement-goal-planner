package com.skillstorm.retirementplanner.services;

import com.skillstorm.retirementplanner.repositories.FundingSourceRepository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.skillstorm.retirementplanner.dtos.ContributionDto;
import com.skillstorm.retirementplanner.models.Contribution;
import com.skillstorm.retirementplanner.models.FundingSource;
import com.skillstorm.retirementplanner.models.Goal;
import com.skillstorm.retirementplanner.models.User;
import com.skillstorm.retirementplanner.repositories.ContributionsRepository;

@Service
public class ContributionService {
    
    private final FundingSourceRepository fundingRepo;
    private final ContributionsRepository repo;
    // private final UserService userService;

    public ContributionService(ContributionsRepository repo, FundingSourceRepository fundingSourceRepository) {
        this.repo = repo;
        this.fundingRepo = fundingSourceRepository;
    }

    /**
     * Get All Method takes in userId, goalId, sourceId and the amount of pages
     * @param userId - used to find all Contributions by User conditionally
     *                  - can find with no UserId for later implementation of admin of some sort
     * @param goalId - used to find all Contributions associated with a given goal
     * @param sourceId - used to find all Contributions associated with a given funding source
     * @param page - tells how many pages to split the response data into
     * @return - returns a Response Entity status wrapped around a page of Contributions
     */
    public ResponseEntity<Page<Contribution>> getAll(Long userId, Long goalId, Long sourceId, int page) {
        Pageable pages = PageRequest.of(page, 6);
        if(userId == null) {
            return ResponseEntity.ok(this.repo.findAll(pages));
        } else {
            if(goalId == null && sourceId == null) {
                return ResponseEntity.ok(this.repo.findByUserId(userId, pages));
            } else if(goalId != null) {
                return ResponseEntity.ok(this.repo.findByGoalId(goalId, userId, pages));
            } 
            return ResponseEntity.ok(this.repo.findBySourceId(sourceId, userId, pages));
        }
    }

    /**
     * Get One Method takes in a userId and contributionId
     * @param userId - determines if the given user is in the database
     * @param id - used to see if a given contribution is attached to the given user
     * @return - returns a contribution if the contribution and user are linked together
     */
    public ResponseEntity<Contribution> getOne(Long userId, Long id) {

        /**
         * logic needed to find user by userId
         *      if(userRepository.existsById(userId)) {
         *          User user = userService.findUserById(userId);
         *      } else {
         *          return ResponseEntity.status(404).build();
         *      }
         */
        Optional<Contribution> temp = this.repo.findOneByUserId(userId, id);
        if(temp.isPresent()) {
            return ResponseEntity.ok(temp.get());
        }
        return ResponseEntity.notFound().build();
        
    }

    /**
     * Create One Method takes in a contribution Dto
     * @param dto - populates a contribution entity with an amount, date, category, notes, 
     *              userId that is used to receive a user, sourceId that is used to receive a source,
     *              and goalId that is used to receive a goal
     * @return - returns a Response Entity status wrapped around a Contribution object
     */
    public ResponseEntity<Contribution> createOne(ContributionDto dto) {

        /**
         * logic needed to find user by userId
         *      if(userRepository.existsById(userId)) {
         *          User user = userService.findUserById(userId);
         *      } else {
         *          return ResponseEntity.status(404).build();
         *      }
         */

        /**
         * logic needed to find goal by goalId
         *      if(goalRepository.existsById(goalId)) {
         *          Goal goal = goalService.findgGoalById(goalId);
         *      } else {
         *          return ResponseEntity.status(404).build();
         *      }
         */

        Optional<FundingSource> temp = fundingRepo.findOneByUserId(dto.userId(), dto.sourceId());
        FundingSource source;
        if(temp.isPresent()) {
            source = temp.get();
        } else {
            return ResponseEntity.status(404).build();
        }

        Contribution contribution = this.repo.save(new Contribution(0L, dto.amount(), dto.date(), dto.category(), dto.notes(),
                                                new User(), new Goal(), source));
        
        return ResponseEntity.status(201).body(contribution);
    }

    /**
     * Update One Method takes in a contribution Id, userId, and a contribution dto
     * @param id - used to identify what contribution is being modified
     * @param userId - used to make sure the current User has access to the given contribution
     * @param dto - populates a contribution entity with an amount, date, category, notes, 
     *              userId that is used to receive a user, sourceId that is used to receive a source,
     *              and goalId that is used to receive a goal
     * @return - returns a Response Entity status code wrapped around a Contribution object
     */
    public ResponseEntity<Contribution> updateOne(Long id, Long userId, ContributionDto dto) {
        
        /**
         * logic needed to find user by userId
         *      if(userRepository.existsById(userId)) {
         *          User user = userService.findUserById(userId);
         *      } else {
         *          return ResponseEntity.status(404).build();
         *      }
         */

        Optional<Contribution> current = this.repo.findOneByUserId(userId, id);
        if(current.isPresent()) {
            Contribution temp = current.get();

            if(dto.amount() != null) temp.setAmount(dto.amount());
            if(dto.category() != null) temp.setCategory(dto.category());
            if(dto.date() != null) temp.setDate(dto.date());
            if(dto.notes() != null) temp.setNotes(dto.notes());

            Contribution updated = this.repo.save(temp);

            return ResponseEntity.ok().body(updated);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete One Method takes in a contribution Id and a userId
     * @param id - used to identify what contribution is being modified
     * @param userId - used to make sure the current User has access to the given contribution
     * @return - returns a no content response on successful delete and a 409 code if the contribution target date 
     *           has already passed preventing deleting past contributions
     */
    public ResponseEntity<Void> deleteOne(Long id, Long userId) {

        /**
         * logic needed to find user by userId
         *      if(userRepository.existsById(userId)) {
         *          User user = userService.findUserById(userId);
         *      } else {
         *          return ResponseEntity.status(404).build();
         *      }
         */

        Optional<Contribution> temp = this.repo.findOneByUserId(userId, id);
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
