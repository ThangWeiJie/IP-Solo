package com.literacyhub.entity;

import javax.persistence.*;

@Entity
@Table(name = "assessment_options")
public class AssessmentOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_type", nullable = false)
    private String testType;

    @Column(name = "option_label", nullable = false)
    private String optionLabel;

    @Column(name = "option_value", nullable = false)
    private Integer optionValue;

    @Column(name = "display_order")
    private Integer displayOrder;

    public AssessmentOption() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getOptionLabel() {
        return optionLabel;
    }

    public void setOptionLabel(String optionLabel) {
        this.optionLabel = optionLabel;
    }

    public Integer getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(Integer optionValue) {
        this.optionValue = optionValue;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
