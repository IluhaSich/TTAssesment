package com.project.tta.services;

public class TimeTable {
    private String name;
    private String link;
    private Integer course;
    private String[][] timeTable;

    public TimeTable(String name, String link, Integer course, String[][] timeTable) {
        this.name = name;
        this.link = link;
        this.course = course;
        this.timeTable = timeTable;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public Integer getCourse() {
        return course;
    }

    public String[][] getTimeTable() {
        return timeTable;
    }
}
