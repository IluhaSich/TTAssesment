package com.project.tta.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "grades")
public class TtGrade extends BaseEntity {
    private String name;
    private Map<String,Integer> grade;

    public TtGrade() {
        this.grade = new HashMap<>(8);
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Map<String, Integer> getGrade() {
        return grade;
    }

    public void setGrade(Map<String, Integer> grade) {
        this.grade = grade;
    }

    public void addGrade(Map<String,Integer> newGrade){
        try {
            var info = newGrade.keySet().stream().findFirst().orElseThrow();
            var value = newGrade.values().stream().findFirst().orElseThrow();
            grade.put(info, value);
        } catch (NullPointerException e) {
            System.out.println("Grade is null");
        }
    }
}