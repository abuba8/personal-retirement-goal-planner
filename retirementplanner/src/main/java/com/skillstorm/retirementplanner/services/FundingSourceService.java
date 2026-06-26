package com.skillstorm.retirementplanner.services;

import com.skillstorm.retirementplanner.dtos.FundingSourceDto;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.skillstorm.retirementplanner.models.FundingSource;
import com.skillstorm.retirementplanner.models.User;
import com.skillstorm.retirementplanner.repositories.FundingSourceRepository;

@Service
public class FundingSourceService {

    private final FundingSourceRepository repo;
    // private final UserService userService;
    // private final ContributionService contributionService;

    public FundingSourceService(FundingSourceRepository repo) {
        this.repo = repo;
    }

    /**
     * Get All Method takes in userId and the amount of pages
     * @param userId - used to find all Funding Sources By User conditionally
     *                      - can find with no UserId for later implementation of admin of some sort
     * @param page - tells how many pages the data should be split into
     * @return - returns a Response Entity status wrapped around a page of funding sources
     */
    public ResponseEntity<Page<FundingSource>> getAll(Long userId, int page) {
        Pageable pages = PageRequest.of(page, 6);
        if(userId != null) {
            /**
             * logic needed to find user by userId
             *      if(userService.existsById(userId)) {
             *          User user = userService.findUserById(userId);
             *      } else {
             *          return ResponseEntity.status(404).build();
             *      }
             */
            return ResponseEntity.ok(this.repo.findByUserId(userId, pages));
        }
        return ResponseEntity.ok(this.repo.findAll(pages));
    }

    /**
     * Get One Method used to find one funding Source by user Id
     * @param userId - used to make sure the given user has the funding source
     * @param id - make sure the funding source given is attached to the user
     * @return - returns a funding source if the source and user are attached together
     */
    public ResponseEntity<FundingSource> getOne(Long userId, Long id) {
        /**
         * logic needed to find user by userId
         *      if(userService.existsById(userId)) {
         *          User user = userService.findUserById(userId);
         *      } else {
         *          return ResponseEntity.status(404).build();
         *      }
         */
        Optional<FundingSource> temp = this.repo.findOneByUserId(userId, id);
        if(temp.isPresent()) {
            return ResponseEntity.ok(temp.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Create One method takes in a funding source dto
     * @param dto - populates a funding source entity with a name, institution, notes, 
     *              userId that is used to recieve a User, and the source Type
     * @return - returns a Response Entity status wrapped around a Funding Source object
     */
    public ResponseEntity<FundingSource> createOne(FundingSourceDto dto) {

        /**
         * logic needed to find user by userId
         *      if(userService.existsById(userId)) {
         *          User user = userService.findUserById(userId);
         *      } else {
         *          return ResponseEntity.status(404).build();
         *      }
         */
        
        // replace new User() with found user
        FundingSource source = this.repo.save(new FundingSource(0L, dto.name(), dto.institution(), dto.notes(), 
                                      new User() , dto.type()));

        return ResponseEntity.status(201).body(source);
    }

    /**
     * Update One method takes in a funding Source Id, User Id, and a Funding Source Dto
     * @param id - used to identify what funding Source is being worked with
     * @param userId - used to make sure the User has access to the source
     * @param dto - populates a funding source entity with a name, institution, notes, 
     *              userId that is used to recieve a User, and the source Type
     * @return - returns a Response Entity status code wrapped around a Funding Source object
     */
    public ResponseEntity<FundingSource> updateOne(Long id, Long userId, FundingSourceDto dto) {

        /**
         * logic needed to find user by userId
         *      if(userService.existsById(userId)) {
         *          User user = userService.findUserById(userId);
         *      } else {
         *          return ResponseEntity.status(404).build();
         *      }
         */
        Optional<FundingSource> current = this.repo.findOneByUserId(userId, id);
        if(current.isPresent()) {
            FundingSource temp = current.get();

            if(dto.name() != null) temp.setName(dto.name());
            if(dto.institution() != null) temp.setInstitution(dto.institution());
            if(dto.notes() != null) temp.setNotes(dto.notes());
            if(dto.type() != null) temp.setSourceType(dto.type());

            FundingSource updated = this.repo.save(temp);

            return ResponseEntity.ok().body(updated);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete One method takes in a funding source id, User id and Contribution Id
     * @param id - used to identify what funding Source is being worked with
     * @param userId - used to make sure the User has access to the source
     * @param contributionId - used to validate that the funding source has no contributions before deletion
     * @return - returns a Respone Entity status wrapped around void because nothing is returned in the delete
     */
    public ResponseEntity<Void> deleteOne(Long id, Long userId, Long contributionId) {

        /**
         * logic needed to find user by userId
         *      if(userService.existsById(userId)) {
         *          User user = userService.findUserById(userId);
         *      } else {
         *          return ResponseEntity.status(404).build();
         *      }
         */

        /**
         * Also need to check if present in Contribution Table
         *      && contributionService.findByFundingSourceId(id).isEmpty();
         */

        if(this.repo.findOneByUserId(userId, id).isPresent()) {
            this.repo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(404).build();
    }
}
