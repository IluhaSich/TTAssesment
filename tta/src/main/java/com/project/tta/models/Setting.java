package com.project.tta.models;

public enum Setting {
    BACHELOR("Бакалавриат"),
    BACHELOR_SENIOR("Бакалавриат (старшие курсы)"),
    MASTER("Магистратура");

    private final String displayName; // Отображаемое имя

    Setting(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
