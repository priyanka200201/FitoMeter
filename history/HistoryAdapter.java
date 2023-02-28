package com.example.fitometer.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitometer.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {
    List<HistoryData> list;
    Context context;

    public HistoryAdapter(List<HistoryData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View historyView = LayoutInflater.from(context).inflate(R.layout.history_layout,parent,false);
        return new HistoryViewHolder(historyView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.date.setText(list.get(position).date);
        holder.value.setText(list.get(position).value);
        holder.valueType.setText(list.get(position).valueType);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
