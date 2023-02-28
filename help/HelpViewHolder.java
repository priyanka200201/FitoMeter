package com.example.fitometer.help;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitometer.R;

public class HelpViewHolder extends RecyclerView.ViewHolder {
    TextView title,description;
    public HelpViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.help_recyclerView_title);
        description = itemView.findViewById(R.id.help_recyclerView_description);
    }
}
