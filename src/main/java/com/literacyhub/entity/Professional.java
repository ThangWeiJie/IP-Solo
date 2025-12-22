package com.literacyhub.entity;

import javax.persistence.*;

@Entity
@Table(name = "professionals")
@PrimaryKeyJoinColumn(name = "user_id")
public class Professional extends User {
    @Column(name = "license_id")
    private String licenseId;

    public Professional() {
        super();
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }
}
