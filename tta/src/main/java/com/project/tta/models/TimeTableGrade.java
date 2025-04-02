package com.project.tta.models;

import java.util.HashMap;
import java.util.Map;

public class TimeTableGrade {
    private String name;
    private Map<String,Integer> grade;

    public TimeTableGrade() {
        this.grade = new HashMap<>(8);
    }

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
        //TODO:
    }
}
