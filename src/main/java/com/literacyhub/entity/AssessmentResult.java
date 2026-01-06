package com.literacyhub.entity;

import javax.persistence.*;

@Entity
@Table(name = "assessment_results")
public class AssessmentResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private UserAssessmentSubmission submission;

    @Column(name = "score_category", nullable = false)
    private String scoreCategory;

    @Column(name = "score_value", nullable = false)
    private Integer scoreValue;

    public AssessmentResult() {}
    public AssessmentResult(UserAssessmentSubmission sub, String cat, Integer val) {
        this.submission = sub;
        this.scoreCategory = cat;
        this.scoreValue = val;
    }
}
