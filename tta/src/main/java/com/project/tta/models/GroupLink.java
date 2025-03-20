package com.project.tta.models;

public class GroupLink {
    private String name;
    private String link;

    public GroupLink(String name, String link) {
        this.name = name;
        this.link = link;
    }

    @Override
    public String toString() {
        return "[Name: "+name+"|link: " +link +"]";
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
