package com.example.fitometer.achievements;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitometer.R;

public class AchievementsViewHolder extends RecyclerView.ViewHolder {
    ImageView icon;
    TextView title;
    TextView description;
    ProgressBar progress;
    TextView progressText;
    ImageView completed;
    CardView cardView;
    public AchievementsViewHolder(@NonNull View itemView) {
        super(itemView);
        icon=itemView.findViewById(R.id.achievement_icon);
        title=itemView.findViewById(R.id.achievement_title);
        description=itemView.findViewById(R.id.achievement_desc);
        progress=itemView.findViewById(R.id.achievement_progress);
        progressText=itemView.findViewById(R.id.achievements_progress_text);
        completed=itemView.findViewById(R.id.completed_icon);
        cardView=itemView.findViewById(R.id.achievement_card_view);
    }
}
