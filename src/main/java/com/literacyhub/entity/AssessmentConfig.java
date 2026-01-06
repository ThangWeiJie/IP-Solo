package com.literacyhub.entity;

import javax.persistence.*;

@Entity
@Table(name = "assessment_configs")
public class AssessmentConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_type", unique = true, nullable = false)
    private String testType;   // e.g. "DASS21"

    @Column(name = "test_name")
    private String testName;   // e.g. "DASS-21 Stress Scale"

    @Column(name = "multiplier")
    private Integer multiplier = 1;

    public AssessmentConfig() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String t) {
        this.testType = t;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String n) {
        this.testName = n;
    }

    public Integer getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Integer m) {
        this.multiplier = m;
    }
}

