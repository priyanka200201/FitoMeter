package com.example.fitometer;

import static android.content.Context.MODE_PRIVATE;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.fitometer.data.DatabaseHelper;
import com.example.fitometer.data.UserInfo;
import com.example.fitometer.history.History;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import me.itangqi.waveloadingview.WaveLoadingView;

public class HomeScreen extends Fragment {
    View view;
    WaveLoadingView stepsUI;
    ImageButton playPauseButton;
    TextView distanceUI,caloriesUI;
    Button stepsGraphButton,distanceGraphButton,caloriesGraphButton;
    DatabaseHelper databaseHelper;
    UserInfo userInfo;
    BarChart barChart;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String[] daysOfWeek={"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};

    int stepCount=0,stepGoal=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_screen, container, false);


        sharedPreferences = requireContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        stepsUI = view.findViewById(R.id.stepsUI);
        playPauseButton=view.findViewById(R.id.playPauseButton);
        distanceUI=view.findViewById(R.id.distance);
        caloriesUI=view.findViewById(R.id.calories);
        barChart=view.findViewById(R.id.barChart);
        stepsGraphButton=view.findViewById(R.id.stepsGraphButton);
        distanceGraphButton=view.findViewById(R.id.distanceGraphButton);
        caloriesGraphButton=view.findViewById(R.id.caloriesGraphButton);
        databaseHelper=new DatabaseHelper(requireContext());
        userInfo=databaseHelper.getUserInfo();
        updateBarGraph();

        playPauseButton.setOnClickListener(view -> {
            //paused
            if(sharedPreferences.getBoolean("running",false)) {
                editor.putBoolean("running", false);
                playPauseButton.setImageResource(R.drawable.circular_play_button);
                playPauseButton.setBackgroundResource(R.drawable.circular_play_button);
                stepsUI.pauseAnimation();
                ((MainActivity) requireActivity()).stopCalorieCalculation();
            }
            else{
                editor.putBoolean("running",true);
                playPauseButton.setImageResource(R.drawable.circular_pause_button);
                playPauseButton.setBackgroundResource(R.drawable.circular_pause_button);
                stepsUI.resumeAnimation();
                ((MainActivity) requireActivity()).startCalorieCalculation();
            }
            editor.apply();
        });

        stepsGraphButton.setOnClickListener(view -> {
            stepsGraphButton.setBackgroundColor(Color.TRANSPARENT);
            stepsGraphButton.setTextColor(Color.GREEN);
            distanceGraphButton.setBackgroundResource(R.drawable.bgcolor);
            distanceGraphButton.setTextColor(Color.WHITE);
            caloriesGraphButton.setBackgroundResource(R.drawable.bgcolor);
            caloriesGraphButton.setTextColor(Color.WHITE);

            editor.putString("selectedGraphButton" , "steps");
            editor.apply();

            createStepsBarGraph();
        });

        distanceGraphButton.setOnClickListener(view -> {
            distanceGraphButton.setBackgroundColor(Color.TRANSPARENT);
            distanceGraphButton.setTextColor(Color.GREEN);
            stepsGraphButton.setBackgroundResource(R.drawable.bgcolor);
            stepsGraphButton.setTextColor(Color.WHITE);
            caloriesGraphButton.setBackgroundResource(R.drawable.bgcolor);
            caloriesGraphButton.setTextColor(Color.WHITE);

            editor.putString("selectedGraphButton" , "distance");
            editor.apply();

            createDistanceBarGraph();
        });

        caloriesGraphButton.setOnClickListener(view -> {
            caloriesGraphButton.setBackgroundColor(Color.TRANSPARENT);
            caloriesGraphButton.setTextColor(Color.GREEN);
            stepsGraphButton.setBackgroundResource(R.drawable.bgcolor);
            stepsGraphButton.setTextColor(Color.WHITE);
            distanceGraphButton.setBackgroundResource(R.drawable.bgcolor);
            distanceGraphButton.setTextColor(Color.WHITE);

            editor.putString("selectedGraphButton" , "calories");
            editor.apply();

            createCaloriesBarGraph();
        });

        barChart.setOnClickListener(v -> {
            History history = new History();
            getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, history).addToBackStack(history.getTag()).setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out).commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStepsUI();
        updateCalories();
        if(!sharedPreferences.getBoolean("running",false)){
            playPauseButton.setImageResource(R.drawable.circular_play_button);
            playPauseButton.setBackgroundResource(R.drawable.circular_play_button);
            stepsUI.pauseAnimation();
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateStepsUI() {
        stepCount = databaseHelper.getStepsForDaysBefore(0);
        stepGoal=databaseHelper.getUserInfo().getStepGoal();
        stepsUI.setProgressValue((int)(((double)stepCount/(double)stepGoal)*100));
        stepsUI.setCenterTitle(String.valueOf(stepCount));
        stepsUI.setTopTitle("Goal: "+stepGoal);
        distanceUI.setText(String.format(Locale.getDefault(),"%.2f",databaseHelper.getDistanceForDaysBefore(0)) +" Km");
    }

    @SuppressLint("SetTextI18n")
    public void updateCalories() {
        caloriesUI.setText(String.format(Locale.getDefault(),"%.2f",databaseHelper.getCaloriesForDaysBefore(0))+" Kcal");
        updateBarGraph();
    }

    private void updateBarGraph() {
        String selected = sharedPreferences.getString("selectedGraphButton","steps");
        switch (selected) {
            case "steps":
                createStepsBarGraph();
                break;
            case "distance":
                createDistanceBarGraph();
                break;
            case "calories":
                createCaloriesBarGraph();
                break;
        }
    }

    void createStepsBarGraph(){
        ArrayList<String> weekdays=new ArrayList<>();
        ArrayList<BarEntry> weeklySteps=new ArrayList<>();
        for(int i=6;i>=0;i--){
            Calendar calendar=Calendar.getInstance();
            calendar.add(Calendar.DATE,-i);
            weekdays.add(daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK)-1]);
            weeklySteps.add(new BarEntry((6-i),databaseHelper.getStepsForDaysBefore(i)));
        }
        BarDataSet barDataSet=new BarDataSet(weeklySteps,"Steps");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        BarData stepData=new BarData(barDataSet);
        stepData.setValueTextColor(Color.WHITE);
        barChart.setData(stepData);
        XAxis xAxis=barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weekdays));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setDragEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setVisibleYRangeMinimum(0,YAxis.AxisDependency.LEFT);
        barChart.invalidate();
    }

    void createDistanceBarGraph(){
        ArrayList<String> weekdays=new ArrayList<>();
        ArrayList<BarEntry> weeklyDistance =new ArrayList<>();
        for(int i=6;i>=0;i--){
            Calendar calendar=Calendar.getInstance();
            calendar.add(Calendar.DATE,-i);
            weekdays.add(daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK)-1]);
            weeklyDistance.add(new BarEntry((6-i), databaseHelper.getDistanceForDaysBefore(i)));
        }
        BarDataSet barDataSet=new BarDataSet(weeklyDistance,"Distance Travelled");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        barDataSet.setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(),"%.2f",value);            }
        });
        BarData distanceData =new BarData(barDataSet);
        distanceData.setValueTextColor(Color.WHITE);
        barChart.setData(distanceData);
        XAxis xAxis=barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weekdays));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setDragEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setVisibleYRangeMinimum(0.0f,YAxis.AxisDependency.LEFT);
        barChart.invalidate();
    }
    void createCaloriesBarGraph(){
        ArrayList<String> weekdays=new ArrayList<>();
        ArrayList<BarEntry> weeklyCalories =new ArrayList<>();
        for(int i=6;i>=0;i--){
            Calendar calendar=Calendar.getInstance();
            calendar.add(Calendar.DATE,-i);
            weekdays.add(daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK)-1]);
            weeklyCalories.add(new BarEntry((6-i), databaseHelper.getCaloriesForDaysBefore(i)));
        }
        BarDataSet barDataSet=new BarDataSet(weeklyCalories,"Calories Burnt");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        barDataSet.setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(),"%.2f",value);            }
        });
        BarData caloriesData =new BarData(barDataSet);
        caloriesData.setValueTextColor(Color.WHITE);
        barChart.setData(caloriesData);
        XAxis xAxis=barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weekdays));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setDragEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setVisibleYRangeMinimum(0.0f,YAxis.AxisDependency.LEFT);
        barChart.invalidate();
    }
}