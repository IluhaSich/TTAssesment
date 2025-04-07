package com.project.tta.models;

import jakarta.persistence.*;

@Entity
@Table(name = "groups")
public class Group extends BaseEntity{
    private String name;
    private String link;
    private Integer course;
    private TTEvaluation TTEvaluation;

    protected Group() {}

    public Group(String name, String link, Integer course, TTEvaluation TTEvaluation) {
        this.name = name;
        this.link = link;
        this.course = course;
        this.TTEvaluation = TTEvaluation;
    }

    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public Integer getCourse() {
        return course;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tt_evaluation_id")
    public TTEvaluation getTTEvaluation() {
        return TTEvaluation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTTEvaluation(TTEvaluation TTEvaluation) {
        this.TTEvaluation = TTEvaluation;
    }

    public void setCourse(Integer course) {
        this.course = course;
    }

}
