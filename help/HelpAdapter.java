package com.example.fitometer.help;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitometer.R;

import java.util.List;

public class HelpAdapter extends RecyclerView.Adapter<HelpViewHolder> {
    List<HelpData> list;
    Context context;

    public HelpAdapter(List<HelpData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public HelpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View helpView = LayoutInflater.from(parent.getContext()).inflate(R.layout.help_layout,parent,false);
        return new HelpViewHolder(helpView);
    }

    @Override
    public void onBindViewHolder(@NonNull HelpViewHolder holder, int position) {
        holder.title.setText(list.get(position).title);
        holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_expand_more_24,0);
        holder.description.setText(list.get(position).description);
        holder.title.setOnClickListener(v ->{
            if(holder.description.getVisibility()==View.GONE){
                holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_expand_less_24,0);
                holder.description.setVisibility(View.VISIBLE);
            }
            else{
                holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_expand_more_24,0);
                holder.description.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
