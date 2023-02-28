package com.example.fitometer.achievements;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitometer.R;

import java.util.List;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsViewHolder> {

    List<AchievementsData> list;
    Context context;

    public AchievementsAdapter(List<AchievementsData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public AchievementsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View achievementView = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievements_layout,parent,false);
        return new AchievementsViewHolder(achievementView);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementsViewHolder holder, int position) {
        holder.icon.setImageResource(list.get(position).icon);
        holder.title.setText(list.get(position).title);
        holder.description.setText(list.get(position).description);
        holder.progress.setProgress(list.get(position).progress);
        holder.progressText.setText(list.get(position).progress_text);
        if(list.get(position).progress >= 100) {
            holder.completed.setVisibility(View.VISIBLE);
            holder.cardView.setBackgroundResource(R.drawable.bgcolor);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
