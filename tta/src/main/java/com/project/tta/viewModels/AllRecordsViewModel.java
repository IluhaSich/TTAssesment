package com.project.tta.viewModels;

public class AllRecordsViewModel {
    private String groupName;
    private String link;
    private String criterionName;
    private int score;

    public AllRecordsViewModel(String groupName, String link, String criterionName, int score) {
        this.groupName = groupName;
        this.link = link;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
