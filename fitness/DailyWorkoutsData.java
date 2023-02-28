package com.example.fitometer.fitness;

public class DailyWorkoutsData {
    int icon;
    String title;
    String description;
    double calories;
    int duration;
    String detailedDescription;
    int durationPerSet;

    public DailyWorkoutsData(int icon, String title, String description, double calories, int duration, String detailedDescription, int durationPerSet) {
        this.icon = icon;
        this.title = title;
        this.description = description;
        this.calories = calories;
        this.duration = duration;
        this.detailedDescription = detailedDescription;
        this.durationPerSet = durationPerSet;
    }
}
