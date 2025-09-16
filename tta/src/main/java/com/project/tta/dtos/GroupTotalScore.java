package com.project.tta.dtos;

import com.project.tta.models.Setting;

public class GroupTotalScore {
    private String groupName;
    private String link;
    private Setting setting;
    private int totalScore;

    public GroupTotalScore(String groupName, String link, Setting setting, int totalScore) {
        this.groupName = groupName;
        this.link = link;
        this.setting = setting;
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

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }
}
