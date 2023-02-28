package com.example.fitometer.history;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.fitometer.data.DatabaseHelper;
import com.example.fitometer.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class History extends Fragment {

    View view;
    Button stepsHistoryButton,distanceHistoryButton,caloriesHistoryButton;
    RecyclerView historyRecyclerView;
    ImageButton historyBackButton;
    Spinner historyGroupBy;
    DatabaseHelper databaseHelper;
    List<HistoryData> list,listByDate;
    int selectedButton;
    String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    String[] monthsFull = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history, container, false);

        stepsHistoryButton = view.findViewById(R.id.stepsHistoryButton);
        distanceHistoryButton = view.findViewById(R.id.distanceHistoryButton);
        caloriesHistoryButton = view.findViewById(R.id.caloriesHistoryButton);

        historyRecyclerView = view.findViewById(R.id.history_recycler_view);

        historyBackButton = view.findViewById(R.id.historyBackButton);

        historyGroupBy = view.findViewById(R.id.history_groupBy);

        databaseHelper = new DatabaseHelper(requireContext());

        list = loadStepsHistory();

        listByDate = new ArrayList<>();
        listByDate.addAll(list);

        HistoryAdapter adapter = new HistoryAdapter(list,requireContext());
        historyRecyclerView.setAdapter(adapter);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        stepsHistoryButton.setOnClickListener(v ->{
            if(selectedButton != R.id.stepsHistoryButton) {
                stepsHistoryButton.setBackgroundColor(Color.TRANSPARENT);
                stepsHistoryButton.setTextColor(Color.GREEN);
                distanceHistoryButton.setBackgroundResource(R.drawable.bgcolor);
                distanceHistoryButton.setTextColor(Color.WHITE);
                caloriesHistoryButton.setBackgroundResource(R.drawable.bgcolor);
                caloriesHistoryButton.setTextColor(Color.WHITE);
                historyGroupBy.setSelection(0);

                list.clear();
                list.addAll(loadStepsHistory());
                adapter.notifyDataSetChanged();

                listByDate.clear();
                listByDate.addAll(list);
            }
        });

        distanceHistoryButton.setOnClickListener(v ->{
            if(selectedButton != R.id.distanceHistoryButton){
                distanceHistoryButton.setBackgroundColor(Color.TRANSPARENT);
                distanceHistoryButton.setTextColor(Color.GREEN);
                stepsHistoryButton.setBackgroundResource(R.drawable.bgcolor);
                stepsHistoryButton.setTextColor(Color.WHITE);
                caloriesHistoryButton.setBackgroundResource(R.drawable.bgcolor);
                caloriesHistoryButton.setTextColor(Color.WHITE);
                historyGroupBy.setSelection(0);

                list.clear();
                list.addAll(loadDistanceHistory());
                adapter.notifyDataSetChanged();

                listByDate.clear();
                listByDate.addAll(list);
            }
        });

        caloriesHistoryButton.setOnClickListener(v ->{
            if(selectedButton != R.id.caloriesHistoryButton){
                caloriesHistoryButton.setBackgroundColor(Color.TRANSPARENT);
                caloriesHistoryButton.setTextColor(Color.GREEN);
                stepsHistoryButton.setBackgroundResource(R.drawable.bgcolor);
                stepsHistoryButton.setTextColor(Color.WHITE);
                distanceHistoryButton.setBackgroundResource(R.drawable.bgcolor);
                distanceHistoryButton.setTextColor(Color.WHITE);
                historyGroupBy.setSelection(0);

                list.clear();
                list.addAll(loadCaloriesHistory());
                adapter.notifyDataSetChanged();

                listByDate.clear();
                listByDate.addAll(list);
            }
        });

        historyBackButton.setOnClickListener(v -> getParentFragmentManager().popBackStackImmediate());

        historyGroupBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        list.clear();
                        list.addAll(listByDate);
                        adapter.notifyDataSetChanged();
                        break;
                    case 1:
                        list.clear();
                        list.addAll(groupByWeek());
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        list.clear();
                        list.addAll(groupByMonth());
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return view;
    }

    private List<HistoryData> loadStepsHistory() {
        selectedButton = R.id.stepsHistoryButton;

        List<HistoryData> stepsData = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int i=0;
        for(Calendar c = Calendar.getInstance();c.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);c.add(Calendar.DATE,-1)){
            stepsData.add(new HistoryData(months[c.get(Calendar.MONTH)]+" "+c.get(Calendar.DAY_OF_MONTH),
                    String.valueOf(databaseHelper.getStepsForDaysBefore(i)),
                    "steps"));
            i++;
        }
        return stepsData;
    }

    private List<HistoryData> loadDistanceHistory() {
        selectedButton = R.id.distanceHistoryButton;

        List<HistoryData> distanceData = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int i=0;
        for(Calendar c = Calendar.getInstance();c.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);c.add(Calendar.DATE,-1)){
            distanceData.add(new HistoryData(months[c.get(Calendar.MONTH)]+" "+c.get(Calendar.DAY_OF_MONTH),
                    String.format(Locale.getDefault(),"%.2f",databaseHelper.getDistanceForDaysBefore(i)),
                    "Km"));
            i++;
        }
        return distanceData;
    }

    private List<HistoryData> loadCaloriesHistory() {
        selectedButton = R.id.caloriesHistoryButton;

        List<HistoryData> caloriesData = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int i=0;
        for(Calendar c = Calendar.getInstance();c.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);c.add(Calendar.DATE,-1)){
            caloriesData.add(new HistoryData(months[c.get(Calendar.MONTH)]+" "+c.get(Calendar.DAY_OF_MONTH),
                    String.format(Locale.getDefault(),"%.2f",databaseHelper.getCaloriesForDaysBefore(i)),
                    "Kcal"));
            i++;
        }
        return caloriesData;
    }

    private List<HistoryData> groupByWeek(){
        List<HistoryData> listByWeek = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        String week,totalText;
        int no_of_days;
        int i = 0;
        Calendar c = Calendar.getInstance();
        while(true){
            if(listByWeek.isEmpty()){
                week = "This Week";
                no_of_days = c.get(Calendar.DAY_OF_WEEK);
            }
            else if (listByWeek.size()==1){
                week = "Last Week";
                no_of_days = 6;
            }
            else {
                week = months[c.get(Calendar.MONTH)]+" "+c.get(Calendar.DAY_OF_MONTH);
                no_of_days = 6;
            }
            double total=0;
            for(int j = 0;j<=no_of_days;j++){
                total+=Double.parseDouble(listByDate.get(i+j).value);
                c.add(Calendar.DATE,-1);
                if(c.get(Calendar.YEAR) != calendar.get(Calendar.YEAR))
                    return listByWeek;
            }
            i+=no_of_days;
            if(listByWeek.size()>1)
                week = months[c.get(Calendar.MONTH)]+" "+c.get(Calendar.DAY_OF_MONTH) + " - " + week;
            if(selectedButton == R.id.stepsHistoryButton)
                totalText = String.valueOf((int)total);
            else
                totalText = String.format(Locale.getDefault(),"%.2f",total);
            listByWeek.add(new HistoryData(week, totalText,listByDate.get(i).valueType));
        }
    }

    private List<HistoryData> groupByMonth(){
        List<HistoryData> listByMonth = new ArrayList<>();
        String currMonth=listByDate.get(0).date.substring(0,3);
        double total=0;
        String totalText;
        for(int i = 0;i<listByDate.size();i++){
            if(listByDate.get(i).date.substring(0, 3).equals(currMonth)){
                total+=Double.parseDouble(listByDate.get(i).value);
            }
            else{
                if(selectedButton == R.id.stepsHistoryButton)
                    totalText = String.valueOf((int)total);
                else
                    totalText = String.format(Locale.getDefault(),"%.2f",total);
                listByMonth.add(new HistoryData(monthsFull[getPosition(months,currMonth)],totalText,listByDate.get(i).valueType));
                total = 0;
                currMonth = listByDate.get(i).date.substring(0,3);
            }
        }
        //data for the last month
        if(selectedButton == R.id.stepsHistoryButton)
            totalText = String.valueOf((int)total);
        else
            totalText = String.format(Locale.getDefault(),"%.2f",total);
        listByMonth.add(new HistoryData(monthsFull[getPosition(months,currMonth)],totalText,listByDate.get(0).valueType));
        return listByMonth;
    }

    private int getPosition(String[] months, String currMonth) {
        for(int i = 0; i<months.length;i++)
            if(months[i].equals(currMonth))
                return i;
        return -1;
    }

}