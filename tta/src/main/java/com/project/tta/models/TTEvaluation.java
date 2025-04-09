package com.project.tta.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class TTEvaluation extends BaseEntity {
    private Group group;
    private Integer total_grade;
    private LocalDateTime localDateTime;
    private List<CriterionEvaluation> criterionEvaluationList;

    protected TTEvaluation() {
    }

    public TTEvaluation(Group group, Integer total_grade, LocalDateTime localDateTime, List<CriterionEvaluation> criterionEvaluationList) {
        this.group = group;
        this.total_grade = total_grade;
        this.localDateTime = localDateTime;
        this.criterionEvaluationList = criterionEvaluationList;
    }

    @OneToOne(mappedBy = "TTEvaluation", cascade = CascadeType.ALL)
    public Group getGroup() {
        return group;
    }

    public Integer getTotal_grade() {
        return total_grade;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    @OneToMany(mappedBy = "TTEvaluation", cascade = CascadeType.ALL)
    public List<CriterionEvaluation> getCriterionEvaluationList() {
        return criterionEvaluationList;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setTotal_grade(Integer total_grade) {
        this.total_grade = total_grade;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public void setCriterionEvaluationList(List<CriterionEvaluation> criterionEvaluationList) {
        this.criterionEvaluationList = criterionEvaluationList;
    }

}
