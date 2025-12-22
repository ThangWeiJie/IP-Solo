package com.literacyhub.entity;

import javax.persistence.*;

@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {
    @Column(name = "staff_id", nullable = false, unique = true)
    private String staffId;

    public Admin() {
        super();
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
}
