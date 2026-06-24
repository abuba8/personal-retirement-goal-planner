package com.skillstorm.retirementplanner.models;

import com.skillstorm.retirementplanner.models.enums.SourceType;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


/**
 * FundingSource Class
 *      - Defined database columns: id, name, institution, notes, and user_id
 *      - all variabiables include Validation as needed
 * 
 *      - Defined no param Constructor and full param constructor
 *      = Defined all proper Getter/Setter methods
 */
@Entity
@Table(name = "funding_source")
public class FundingSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    private String institution;

    private String notes;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @NotNull
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "source_type")
    @NotNull
    private SourceType sourceType;

    public FundingSource() {
    }

    public FundingSource(Long id, String name, String institution, String notes, User user, SourceType sourceType) {
        this.id = id;
        this.name = name;
        this.institution = institution;
        this.notes = notes;
        this.user = user;
        this.sourceType = sourceType;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getInstitution() {
        return institution;
    }
    public void setInstitution(String institution) {
        this.institution = institution;
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
    public SourceType getSourceType() {
        return sourceType;
    }
    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

}
