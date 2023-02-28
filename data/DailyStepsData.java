package com.example.fitometer.data;

public class DailyStepsData {
    String date;
    int steps;
    double distance;

    public DailyStepsData(String date, int steps, double distance) {
        this.date = date;
        this.steps = steps;
        this.distance = distance;
    }
}
