package com.example.fitometer.fitness;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitometer.R;

public class DailyWorkoutsViewHolder extends RecyclerView.ViewHolder {
    ImageView icon;
    TextView title,description,calories,duration,completedIcon;
    CardView cardView;
    public DailyWorkoutsViewHolder(@NonNull View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.dailyWorkout_icon);
        title = itemView.findViewById(R.id.dailyWorkout_title);
        description = itemView.findViewById(R.id.dailyWorkout_description);
        calories = itemView.findViewById(R.id.dailyWorkout_calories);
        duration = itemView.findViewById(R.id.dailyWorkout_duration);
        completedIcon = itemView.findViewById(R.id.dailyWorkout_completedIcon);
        cardView = itemView.findViewById(R.id.dailyWorkouts_cardView);
    }
}
