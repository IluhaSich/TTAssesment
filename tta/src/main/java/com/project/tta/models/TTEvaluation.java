package com.project.tta.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class TTEvaluation extends BaseEntity {
    private Group group;
    private Integer totalGrade;
    private LocalDateTime localDateTime;
    private List<CriterionEvaluation> criterionEvaluationList;

    public TTEvaluation() {
    }

    public TTEvaluation(Group group, Integer totalGrade, LocalDateTime localDateTime, List<CriterionEvaluation> criterionEvaluationList) {
        this.group = group;
        this.totalGrade = totalGrade;
        this.localDateTime = localDateTime;
        this.criterionEvaluationList = criterionEvaluationList;
    }

//    @OneToOne(mappedBy = "TTEvaluation", cascade = CascadeType.ALL) // было изначально

//    @OneToOne
//    @JoinColumn(name = "group_id", nullable = false, unique = true)

    @OneToOne @JoinColumn(name="group_id")
    public Group getGroup() {
        return group;
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

    public Integer getTotalGrade() {
        return totalGrade;
    }



    public void setTotalGrade(Integer total_grade) {
        this.totalGrade = total_grade;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public void setCriterionEvaluationList(List<CriterionEvaluation> criterionEvaluationList) {
        this.criterionEvaluationList = criterionEvaluationList;
    }

    public void setTotal_grade(Integer total_grade) {
        this.totalGrade = total_grade;
    }
}
