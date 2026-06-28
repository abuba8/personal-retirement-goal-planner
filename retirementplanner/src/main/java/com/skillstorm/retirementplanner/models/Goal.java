package com.skillstorm.retirementplanner.models;
import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "goal")
public class Goal {
    /**
     * Goal Entity Class:
     * Each field maps to a column, along with the validation annotations.
     * 
     * Fields: 
     * id, user (ManyToOne), name, targetRetirementAge, targetAmount, notes
     * - targetAmount is BigDecimal (money is never a float/double).
     * - The owning user is a ManyToOne with LAZY fetch.
     * 
     * Default and parameterized constructor
     * Getters and Setters
     * toString method
     */

    // Entity Columns
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    // Many goals belong to a user, lazy load so we don't load user on every goal
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Goal name is required") @Size(max=150)
    @Column(name = "name", nullable=false, length = 150)
    private String name;

    @NotNull(message="Target Retirement Age is required") 
    @Positive(message="Target Retirement Age must be greater than 0")
    @Column(name = "target_retirement_age", nullable = false)
    private Integer targetRetirementAge;

    @NotNull(message="Target Amount is required") 
    @DecimalMin(value="0.0", inclusive=false) @Digits(integer=13, fraction=2)
    @Column(name = "target_amount", nullable = false)
    private BigDecimal targetAmount;

    @Column(name = "notes")
    private String notes;

    // constructor
    public Goal() {
    }

    // parameterized constructor
    public Goal(Long id, User user, String name, Integer targetRetirementAge, BigDecimal targetAmount, String notes) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.targetRetirementAge = targetRetirementAge;
        this.targetAmount = targetAmount;
        this.notes = notes;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTargetRetirementAge() {
        return targetRetirementAge;
    }

    public void setTargetRetirementAge(Integer targetRetirementAge) {
        this.targetRetirementAge = targetRetirementAge;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // toString method
    @Override
    public String toString() {
        return "Goal [id=" + id + ", name=" + name + ", targetRetirementAge=" + targetRetirementAge
                + ", targetAmount=" + targetAmount + ", notes=" + notes + "]";
    }
    
}
