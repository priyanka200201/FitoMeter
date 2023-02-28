package com.example.fitometer.history;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitometer.R;

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    TextView date,value,valueType;
    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        date = itemView.findViewById(R.id.history_date);
        value = itemView.findViewById(R.id.history_value);
        valueType = itemView.findViewById(R.id.history_value_type);
    }
}
