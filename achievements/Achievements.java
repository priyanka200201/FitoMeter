package com.example.fitometer.achievements;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fitometer.data.DatabaseHelper;
import com.example.fitometer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Achievements extends Fragment {
    View view;
    RecyclerView recyclerView;
    DatabaseHelper databaseHelper;
    List<AchievementsData> list;
    TextView noOfCompletedAchievements;
    int totalSteps,completedAchievements=0;
    float totalDistance,totalCalories;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_achievements, container, false);
        databaseHelper = new DatabaseHelper(requireContext());
        totalSteps = databaseHelper.getTotalSteps();
        totalDistance = databaseHelper.getTotalDistance();
        totalCalories = databaseHelper.getTotalCalories();
        noOfCompletedAchievements = view.findViewById(R.id.noOfCompletedAchievements);

        list = updateAchievements();

        recyclerView=view.findViewById(R.id.achievements_recycler_view);
        AchievementsAdapter adapter=new AchievementsAdapter(list,requireContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        noOfCompletedAchievements.setText("("+completedAchievements+"/"+list.size()+" Completed)");
        return view;
    }

    private List<AchievementsData> updateAchievements() {
        List<AchievementsData> list=new ArrayList<>();

        List<AchievementsData> marathon=getMarathonAchievements();
        List<AchievementsData> traveller=getTravellerAchievements();
        List<AchievementsData> calorieCountdown=getCalorieCountdownAchievements();

        List<List<AchievementsData>> typesOfAchievements=new ArrayList<>();
        typesOfAchievements.add(marathon);
        typesOfAchievements.add(traveller);
        typesOfAchievements.add(calorieCountdown);

        List<AchievementsData> completed=new ArrayList<>();
        //add incomplete achievements
        for(int i = 0 ; i < 3 ; i++) {
            for (int j = 0; j < typesOfAchievements.size(); j++) {
                if (typesOfAchievements.get(j).get(i).progress < 100)
                    list.add(typesOfAchievements.get(j).get(i));
                else
                    completed.add(typesOfAchievements.get(j).get(i));
            }
        }
        //add completed achievements
        list.addAll(completed);
        completedAchievements=completed.size();

        return list;
    }

    public List<AchievementsData> getMarathonAchievements(){
        List<AchievementsData> marathon=new ArrayList<>();
        marathon.add(new AchievementsData(R.drawable.run_logo,
                "Marathon I",
                "Walk a total of 10,000 steps.",
                (int) (((double)totalSteps/10000)*100),
                totalSteps+"/10000"));
        marathon.add(new AchievementsData(R.drawable.run_logo,
                "Marathon II",
                "Walk a total of 50,000 steps.",
                (int) (((double)totalSteps/50000)*100),
                totalSteps+"/50000"));
        marathon.add(new AchievementsData(R.drawable.run_logo,
                "Marathon III",
                "Walk a total of 100,000 steps.",
                (int) (((double)totalSteps/100000)*100),
                totalSteps+"/100000"));
        return marathon;
    }
    public List<AchievementsData> getTravellerAchievements(){
        List<AchievementsData> traveller = new ArrayList<>();
        traveller.add(new AchievementsData(R.drawable.traveller,
                "Traveller I",
                "Cover a total distance of 10 Kms.",
                (int)((totalDistance/10)*100),
                String.format(Locale.getDefault(),"%.2f",totalDistance)+"/10.00"));
        traveller.add(new AchievementsData(R.drawable.traveller,
                "Traveller II",
                "Cover a total distance of 50 Kms.",
                (int)((totalDistance/50)*100),
                String.format(Locale.getDefault(),"%.2f",totalDistance)+"/50.00"));
        traveller.add(new AchievementsData(R.drawable.traveller,
                "Traveller III",
                "Cover a total distance of 100 Kms.",
                (int)((totalDistance/100)*100),
                String.format(Locale.getDefault(),"%.2f",totalDistance)+"/100.00"));
        return traveller;
    }
    public List<AchievementsData> getCalorieCountdownAchievements(){
        List<AchievementsData> calorieCountdown=new ArrayList<>();
        calorieCountdown.add(new AchievementsData(R.drawable.ic_baseline_calorie,
                "Calorie Countdown I",
                "Burn a total of 500 KCal.",
                (int)((totalCalories/500)*100),
                String.format(Locale.getDefault(),"%.2f",totalCalories)+"/500.00"));

        calorieCountdown.add(new AchievementsData(R.drawable.ic_baseline_calorie,
                "Calorie Countdown II",
                "Burn a total of 2500 KCal.",
                (int)((totalCalories/2500)*100),
                String.format(Locale.getDefault(),"%.2f",totalCalories)+"/2500.00"));

        calorieCountdown.add(new AchievementsData(R.drawable.ic_baseline_calorie,
                "Calorie Countdown III",
                "Burn a total of 5000 KCal.",
                (int)((totalCalories/5000)*100),
                String.format(Locale.getDefault(),"%.2f",totalCalories)+"/5000.00"));
        return calorieCountdown;
    }
}