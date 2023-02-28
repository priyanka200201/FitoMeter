package com.example.fitometer.fitness;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fitometer.data.DatabaseHelper;
import com.example.fitometer.R;
import com.example.fitometer.data.UserInfo;
import com.sccomponents.gauges.library.ScArcGauge;
import com.sccomponents.gauges.library.ScPointer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class Fitness extends Fragment {
    View view;
    UserInfo userInfo;
    DatabaseHelper databaseHelper;
    ScArcGauge bmiGauge;
    TextView bmiValue,bmiDesc,noOfCompletedWorkouts;
    CardView bmiDescLayout,bmiLayout;
    ImageView bmiLayoutArrow;
    List<DailyWorkoutsData> list;
    RecyclerView dailyWorkoutView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fitness, container, false);

        databaseHelper = new DatabaseHelper(requireContext());
        userInfo = databaseHelper.getUserInfo();

        list = updateDailyWorkouts();


        dailyWorkoutView = view.findViewById(R.id.daily_workout_recycler_view);
        DailyWorkoutsAdapter dailyWorkoutsAdapter = new DailyWorkoutsAdapter(list,requireContext(),Fitness.this);
        dailyWorkoutView.setAdapter(dailyWorkoutsAdapter);
        dailyWorkoutView.setLayoutManager(new LinearLayoutManager(requireContext()));

        createBmiGauge();

        return view;
    }

    @SuppressLint("SetTextI18n")
    private List<DailyWorkoutsData> updateDailyWorkouts() {
        List <DailyWorkoutsData> list = new ArrayList<>();
        list.add(new DailyWorkoutsData(
                R.drawable.jumping_jacks,
                "Jumping Jacks",
                "Perform Jumping Jacks in 3 sets of 20 seconds each.",
                getCalories(7.7,60),
                90,
                "Stand upright with your legs together, arms at your sides. " +
                        "Bend your knees slightly, and jump into the air. " +
                        "As you jump, spread your legs to be about shoulder-width apart. " +
                        "Stretch your arms out and over your head. Jump back to starting position. Repeat.",
                20));
        list.add(new DailyWorkoutsData(
                R.drawable.push_ups,
                "Push Ups",
                "Perform Push Ups in 3 sets of 30 seconds each.",
                getCalories(8,90),
                135,
                "Get down on all fours, placing your hands slightly wider than your shoulders. " +
                        "Straighten your arms and legs. " +
                        "Lower your body until your chest nearly touches the floor. " +
                        "Pause, then push yourself back up. Repeat.\n" +
                        "Note: You can change to knee push-ups to build strength before attempting a standard push-up.",
                30));
        list.add(new DailyWorkoutsData(
                R.drawable.squats,
                "Squats",
                "Perform Squats in 3 sets of 30 seconds each.",
                getCalories(5.5,90),
                120,
                "Stand straight with feet hip-width apart. " +
                        "Tighten your stomach muscles. " +
                        "Lower down, as if sitting in an invisible chair. " +
                        "Straighten your legs to lift back up. Repeat.",
                30));
        list.add(new DailyWorkoutsData(
                R.drawable.crunches,
                "Crunches",
                "Perform Crunches in 3 sets of 60 seconds each.",
                getCalories(5,180),
                240,
                "Lie down on your back. Plant your feet on the floor, hip-width apart. " +
                        "Bend your knees and place your arms across your chest. Contract your abs and inhale. " +
                        "Exhale and lift your upper body, keeping your head and neck relaxed. " +
                        "Inhale and return to the starting position. Repeat.",
                60));
        list.add(new DailyWorkoutsData(
                R.drawable.plank,
                "Plank",
                "Perform Planks in 3 sets of 30 seconds each.",
                getCalories(7,90),
                120,
                "Place forearms on the floor with elbows aligned below " +
                        "shoulders and arms parallel to your body at about shoulder width. " +
                        "Ground toes into the floor and squeeze glutes to stabilize your body. " +
                        "Your head should be in line with your back. " +
                        "Hold the position for 30 seconds. Repeat.",
                30));
        list.add(new DailyWorkoutsData(
                R.drawable.lunges,
                "Stationary Lunges",
                "Perform Lunges in 3 sets of 45 seconds each.",
                getCalories(4,135),
                180,
                "Stand up straight, feet hip-width apart. " +
                        "Put your hands on your hips for stability. Tighten your abs. " +
                        "Shift your weight forward as you take one big step in front of you, allowing your back heel to rise. " +
                        "Sink until your forward-stepping leg is at a right angle. " +
                        "Press into your front heel while you push back up into the starting position. " +
                        "Repeat on the other side.",
                45));

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH,1);
        String todayDate=calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR);

        List<DailyWorkoutsData> workouts = new ArrayList<>();
        List<DailyWorkoutsData> completed = new ArrayList<>();

        for(int i = 0; i<list.size(); i++){
            if(sharedPreferences.getString(list.get(i).title,"").equals(todayDate))
                completed.add(list.get(i));
            else
                workouts.add(list.get(i));
        }
        workouts.addAll(completed);

        noOfCompletedWorkouts = view.findViewById(R.id.noOfCompletedWorkouts);
        noOfCompletedWorkouts.setText("("+completed.size()+"/"+workouts.size()+" Completed)");

        return workouts;
    }

    private double getCalories(double met, int seconds) {
        return (userInfo.calculateBMR()*met*seconds)/86400;
    }

    @SuppressLint("SetTextI18n")
    private void createBmiGauge() {
        double bmi = userInfo.calculateBMI();

        bmiGauge = view.findViewById(R.id.bmiGauge);
        bmiValue = view.findViewById(R.id.bmi_value);
        bmiDesc = view.findViewById(R.id.bmi_desc);
        bmiDescLayout = view.findViewById(R.id.bmiDescLayout);

        bmiValue.setText(String.format(Locale.getDefault(),"%.2f",bmi));

        ScPointer bmiGaugePointer = bmiGauge.getHighPointer();
        double pointerDistance=bmiGaugePointer.getDistance();
        if(bmi < 18.5){
            pointerDistance += bmi/(18.4/20);
            bmiDesc.setText("Underweight");
            bmiDescLayout.setCardBackgroundColor(Color.parseColor("#03DAC5"));
        }
        else if(bmi < 25){
            pointerDistance += 20 + ((bmi-18.4)/(6.5/20));
            bmiDesc.setText("Healthy");
            bmiDescLayout.setCardBackgroundColor(Color.parseColor("#00FF00"));
        }
        else if(bmi < 30){
            pointerDistance += 40 + ((bmi-24.9)/(5.0/20));
            bmiDesc.setText("Overweight");
            bmiDescLayout.setCardBackgroundColor(Color.parseColor("#FFEA00"));
        }
        else if(bmi < 35){
            pointerDistance += 60 + ((bmi-29.9)/(5.0/20));
            bmiDesc.setText("Obese");
            bmiDescLayout.setCardBackgroundColor(Color.parseColor("#FF9100"));
        }
        else{
            pointerDistance += 80 + ((bmi-34.9)/(15.0/20));
            bmiDesc.setText("Morbidly Obese");
            bmiDescLayout.setCardBackgroundColor(Color.parseColor("#FF0000"));
        }
        bmiGauge.setHighValue((float) pointerDistance);
        bmiGaugePointer.setVisible(true);

        //Show/Hide the BMI gauge
        bmiLayout = view.findViewById(R.id.bmiLayout);
        bmiLayoutArrow = view.findViewById(R.id.bmiLayoutArrow);
        bmiLayout.setOnClickListener( v -> {
            if(bmiGauge.getVisibility()==View.VISIBLE)
                bmiGauge.setVisibility(View.GONE);
            else
                bmiGauge.setVisibility(View.VISIBLE);
            bmiLayoutArrow.setRotation((bmiLayoutArrow.getRotation() == 90)?270:90);
        });
    }
    public void refreshFragment(){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView,new Fitness());
        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();
    }
}