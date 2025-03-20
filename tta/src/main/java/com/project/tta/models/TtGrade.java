package com.project.tta.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "grades")
public class TtGrade extends BaseEntity {
    private String name;
    private String grade;

    public TtGrade() {
    }

    public TtGrade(String name, String grade) {
        this.name = name;
        this.grade = grade;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "grade")
    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
