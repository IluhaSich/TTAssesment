package com.project.tta.models;

import jakarta.persistence.*;

@Entity
@Table(name = "groups")
public class Group extends BaseEntity{
    private String name;
    private String courseName;
    private String link;
    private Integer course;
    private Setting setting;
    private TTEvaluation TTEvaluation;

    protected Group() {}

    public Group(String name, String courseName, String link, Integer course, Setting setting, TTEvaluation TTEvaluation) {
        this.name = name;
        this.courseName = courseName;
        this.link = link;
        this.course = course;
        this.setting = setting;
        this.TTEvaluation = TTEvaluation;
    }

    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public String getCourseName() {
        return courseName;
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

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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

    @Enumerated(EnumType.STRING)
    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", courseName='" + courseName + '\'' +
                ", link='" + link + '\'' +
                ", course=" + course +
                ", setting=" + setting +
                ", TTEvaluation=" + TTEvaluation +
                '}';
    }
}
