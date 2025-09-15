package com.project.tta.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class CriterionEvaluation extends BaseEntity{
    private TTEvaluation ttEvaluation;
    private String criterionName;
    private Integer score;

    public CriterionEvaluation() {}

    public CriterionEvaluation(TTEvaluation ttEvaluation, String criterionName, Integer score) {
        this.ttEvaluation = ttEvaluation;
        this.criterionName = criterionName;
        this.score = score;
    }

    @ManyToOne
//    @JoinColumn(name = "time_table_evaluation_id")
    @JoinColumn(name = "tt_evaluation_id")
    public TTEvaluation getTTEvaluation() {
        return ttEvaluation;
    }

    public String getCriterionName() {
        return criterionName;
    }

    public Integer getScore() {
        return score;
    }

    public void setTTEvaluation(TTEvaluation TTEvaluation) {
        this.ttEvaluation = TTEvaluation;
    }

    public void setCriterionName(String criterionName) {
        this.criterionName = criterionName;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "CriterionEvaluation{" +
                "score=" + score +
                ", criterionName='" + criterionName + '\'' +
                '}';
    }
}
