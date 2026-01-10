package com.literacyhub.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "professionals")
@PrimaryKeyJoinColumn(name = "user_id")
public class Professional extends User {
    @Column(name = "professional_id")
    private String professionalId;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "department")
    private String department;

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "verification_document")
    private String verificationDocument;

    public Professional() {
        super();
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getVerificationDocument() {
        return verificationDocument;
    }

    public void setVerification_document(String verification_document) {
        this.verificationDocument = verificationDocument;
    }
}
