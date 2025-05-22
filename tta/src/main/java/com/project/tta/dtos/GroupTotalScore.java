package com.project.tta.dtos;

public class GroupTotalScore {
    private String groupName;
    private String link;
    private int totalScore;

    public GroupTotalScore(String groupName, String link, int totalScore) {
        this.groupName = groupName;
        this.link = link;
        this.totalScore = totalScore;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}
