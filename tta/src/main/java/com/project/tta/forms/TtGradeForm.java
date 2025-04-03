package com.project.tta.forms;

import java.util.Map;

public class TtGradeForm {
    private String name;
    private Map<String, Integer> grade;

    public TtGradeForm(String name, Map<String, Integer> grade) {
        this.name = name;
        this.grade = grade;
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
}
