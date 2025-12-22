package com.literacyhub.entity;

import javax.persistence.*;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id")
public class Student extends User {
    @Column(nullable = false, unique = true)
    private String email;

    public Student() {
        super();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
