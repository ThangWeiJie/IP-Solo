package com.literacyhub.entity;

import javax.persistence.*;

@Entity
@Table(name = "wellness_tips")
public class WellnessTip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String tip;

    public WellnessTip() {}

    public WellnessTip(String tip) {
        this.tip = tip;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTip() { return tip; }
    public void setTip(String tip) { this.tip = tip; }
}
