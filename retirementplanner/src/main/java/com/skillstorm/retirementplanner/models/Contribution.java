package com.skillstorm.retirementplanner.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.skillstorm.retirementplanner.models.enums.ContributionCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Contribution Class
 *      - Defined database columns: id, amount, contribution_date, category, notes, user_id, goal_id, and funding_source_id
 *      - all variabiables include Validation as needed
 * 
 *      - Defined no param Constructor and full param constructor
 *      = Defined all proper Getter/Setter methods
 */
@Entity
@Table(name = "contribution_record")
public class Contribution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;

    @NotNull
    @Positive
    private BigDecimal amount;

    @Column(name = "contribution_date")
    @NotNull
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ContributionCategory category;

    private String notes;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @NotNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "goal_id", referencedColumnName = "id")
    @NotNull
    private Goal goal;

    @ManyToOne
    @JoinColumn(name = "funding_source_id", referencedColumnName = "id")
    @NotNull
    private FundingSource fundingSource;

    public Contribution() {
    }

    public Contribution(Long id, BigDecimal amount, LocalDate date, ContributionCategory category, String notes,
            User user, Goal goal, FundingSource fundingSource) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.notes = notes;
        this.user = user;
        this.goal = goal;
        this.fundingSource = fundingSource;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public ContributionCategory getCategory() {
        return category;
    }
    public void setCategory(ContributionCategory category) {
        this.category = category;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Goal getGoal() {
        return goal;
    }
    public void setGoal(Goal goal) {
        this.goal = goal;
    }
    public FundingSource getFundingSource() {
        return fundingSource;
    }
    public void setFundingSource(FundingSource fundingSource) {
        this.fundingSource = fundingSource;
    }

}
