package com.project.tta.viewModels;

public class AllRecordsViewModel {
    private String groupName;
    private String criterionName;
    private int score;

    public AllRecordsViewModel(String groupName, String criterionName, int score) {
        this.groupName = groupName;
        this.criterionName = criterionName;
        this.score = score;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCriterionName() {
        return criterionName;
    }

    public void setCriterionName(String criterionName) {
        this.criterionName = criterionName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
