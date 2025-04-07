package com.project.tta.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class CriterionEvaluation extends BaseEntity{
    private TTEvaluation TTEvaluation;
    private String criterionName;
    private Integer score;

    protected CriterionEvaluation() {}

    public CriterionEvaluation(TTEvaluation TTEvaluation, String criterionName, Integer score) {
        this.TTEvaluation = TTEvaluation;
        this.criterionName = criterionName;
        this.score = score;
    }

    @ManyToOne
    @JoinColumn(name = "time_table_evaluation_id")
    public TTEvaluation getTTEvaluation() {
        return TTEvaluation;
    }

    public String getCriterionName() {
        return criterionName;
    }

    public Integer getScore() {
        return score;
    }

    public void setTTEvaluation(TTEvaluation TTEvaluation) {
        this.TTEvaluation = TTEvaluation;
    }

    public void setCriterionName(String criterionName) {
        this.criterionName = criterionName;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

}
