package com.example.fitometer.achievements;

public class AchievementsData {
    int icon;
    String title;
    String description;
    int progress;
    String progress_text;

    public AchievementsData(int icon, String title, String description, int progress, String progress_text) {
        this.icon = icon;
        this.title = title;
        this.description = description;
        this.progress = progress;
        this.progress_text = progress_text;
    }
}
