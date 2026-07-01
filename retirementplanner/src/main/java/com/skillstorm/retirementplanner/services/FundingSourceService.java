package com.skillstorm.retirementplanner.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.skillstorm.retirementplanner.dtos.FundingSourceRequest;
import com.skillstorm.retirementplanner.dtos.FundingSourceResponse;
import com.skillstorm.retirementplanner.mappers.FundingSourceMapper;
import com.skillstorm.retirementplanner.models.Contribution;
import com.skillstorm.retirementplanner.models.FundingSource;
import com.skillstorm.retirementplanner.models.User;
import com.skillstorm.retirementplanner.repositories.ContributionRepository;
import com.skillstorm.retirementplanner.repositories.FundingSourceRepository;
import com.skillstorm.retirementplanner.repositories.UserRepository;

@Service
public class FundingSourceService {

    private final ContributionRepository contributionRepo;
    private final FundingSourceRepository repo;
    private final UserRepository userRepo;
    private final FundingSourceMapper sourceMapper;
    private static final int PAGE_SIZE = 10;

    

    public FundingSourceService(ContributionRepository contributionRepo, FundingSourceRepository repo,
            UserRepository userRepo, FundingSourceMapper sourceMapper) {
        this.contributionRepo = contributionRepo;
        this.repo = repo;
        this.userRepo = userRepo;
        this.sourceMapper = sourceMapper;
    }

    /**
     * Get All Method takes in userId and the amount of pages
     * @param userId - used to find all Funding Sources By User conditionally
     *                      - can find with no UserId for later implementation of admin of some sort
     * @param page - tells how many pages the data should be split into
     * @return - returns a Response Entity status wrapped around a page of funding sources
     */
    public ResponseEntity<Page<FundingSourceResponse>> getAll(Long userId, int page) {
        
        Pageable pages = PageRequest.of(page, PAGE_SIZE, Sort.by("id"));

        if(userId != null) {
            return ResponseEntity.ok(this.repo.findByUserId(userId, pages)
                .map(this.sourceMapper::toDto));
        }
        return ResponseEntity.ok(this.repo.findAll(pages).map(this.sourceMapper::toDto));
    }

    /**
     * Get One Method used to find one funding Source by user Id
     * @param userId - used to make sure the given user has the funding source
     * @param id - make sure the funding source given is attached to the user
     * @return - returns a funding source if the source and user are attached together
     */
    public ResponseEntity<FundingSourceResponse> getOne(Long id, Long userId) {
        
        Optional<FundingSource> temp = this.repo.findByIdAndUserId(id, userId);
        if(temp.isPresent()) {
            return ResponseEntity.ok(this.sourceMapper.toDto(temp.get()));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Create One method takes in a funding source dto
     * @param dto - populates a funding source entity with a name, institution, notes, 
     *              userId that is used to recieve a User, and the source Type
     * @return - returns a Response Entity status wrapped around a Funding Source object
     */
    public ResponseEntity<FundingSourceResponse> createOne(Long userId, FundingSourceRequest dto) {

        Optional<User> userObj = this.userRepo.findById(userId);
        if(userObj.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        FundingSource source = this.repo.save(new FundingSource(null, dto.name(), dto.institution(), dto.notes(), userObj.get(), dto.type()));

        return ResponseEntity.status(201).body(this.sourceMapper.toDto(source));
    }

    /**
     * Update One method takes in a funding Source Id, User Id, and a Funding Source Dto
     * @param id - used to identify what funding Source is being worked with
     * @param userId - used to make sure the User has access to the source
     * @param dto - populates a funding source entity with a name, institution, notes, 
     *              userId that is used to recieve a User, and the source Type
     * @return - returns a Response Entity status code wrapped around a Funding Source object
     */
    public ResponseEntity<FundingSourceResponse> updateOne(Long id, Long userId, FundingSourceRequest dto) {

        Optional<FundingSource> current = this.repo.findByIdAndUserId(id, userId);
        if(current.isPresent()) {
            FundingSource temp = current.get();

            if(dto.name() != null) temp.setName(dto.name());
            if(dto.institution() != null) temp.setInstitution(dto.institution());
            if(dto.notes() != null) temp.setNotes(dto.notes());
            if(dto.type() != null) temp.setSourceType(dto.type());

            FundingSource updated = this.repo.save(temp);

            return ResponseEntity.ok().body(this.sourceMapper.toDto(updated));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete One method takes in a funding source id, User id and Contribution Id
     * @param id - used to identify what funding Source is being worked with
     * @param userId - used to make sure the User has access to the source
     * @return - returns a no Content if delete is successful and 409 code if there are Contributions with the current source Id
     */
    public ResponseEntity<Void> deleteOne(Long id, Long userId) {

        if(this.repo.findByIdAndUserId(id, userId).isPresent()) {

            Pageable pages = PageRequest.of(0, PAGE_SIZE, Sort.by("id"));
            Page<Contribution> contributionPage = this.contributionRepo.findByFundingSourceIdAndUserId(id, userId, pages);
            List<Contribution> contributionList = contributionPage.getContent();

            if(contributionList.isEmpty()) {
                this.repo.deleteById(id);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.status(409).build();
        }
        return ResponseEntity.status(404).build();
    }
}
