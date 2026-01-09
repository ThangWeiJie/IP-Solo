package com.literacyhub.entity;

import javax.persistence.*;

@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {
    @Column(name = "staff_id", nullable = false, unique = true)
    private String staffId;

    @Column(name = "department")
    private String department;

    @Column(name = "permissions")
    private String permissions;

    public Admin() {
        super();
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
