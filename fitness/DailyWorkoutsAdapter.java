package com.example.fitometer.fitness;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitometer.data.DatabaseHelper;
import com.example.fitometer.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DailyWorkoutsAdapter extends RecyclerView.Adapter<DailyWorkoutsViewHolder> {

    Fitness fitnessFragment;
    List<DailyWorkoutsData> list;
    Context context;
    int seconds,restTime;
    DatabaseHelper databaseHelper;
    CountDownTimer setTimer,countDownTimer,restTimer;
    boolean sTimerRunning=false,cTimerRunning=false,rTimerRunning=false;

    Calendar calendar; String todayDate;


    public DailyWorkoutsAdapter(List<DailyWorkoutsData> list, Context context,Fitness fitnessFragment) {
        this.list = list;
        this.context = context;
        this.fitnessFragment = fitnessFragment;
        databaseHelper = new DatabaseHelper(context);
        calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH,1);
        todayDate=calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR);
    }

    @NonNull
    @Override
    public DailyWorkoutsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View dailyWorkoutsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_workouts_layout,parent,false);
        return new DailyWorkoutsViewHolder(dailyWorkoutsView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DailyWorkoutsViewHolder holder, int position) {
        holder.icon.setImageResource(list.get(position).icon);
        holder.title.setText(list.get(position).title);
        holder.description.setText(list.get(position).description);
        holder.calories.setText(String.format(Locale.getDefault(),"%.2f",list.get(position).calories)+" KCal");
        holder.duration.setText(list.get(position).duration+" seconds");

        SharedPreferences sharedPreferences = context.getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
        if(sharedPreferences.getString(list.get(position).title,"").equals(todayDate))
            holder.completedIcon.setVisibility(View.VISIBLE);

        holder.cardView.setOnClickListener(view -> {
            int index = holder.getAdapterPosition();

            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.daily_workout_dialog);
            Window window = dialog.getWindow();
            window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);

            ImageView icon = dialog.getWindow().findViewById(R.id.workoutDialog_icon);
            TextView title = dialog.getWindow().findViewById(R.id.workoutDialog_title);
            TextView calories = dialog.getWindow().findViewById(R.id.workoutDialog_calories);
            TextView description = dialog.getWindow().findViewById(R.id.workoutDialog_description);
            TextView setNumber = dialog.getWindow().findViewById(R.id.setNumber);
            TextView timerText = dialog.getWindow().findViewById(R.id.timerText);
            Button button = dialog.getWindow().findViewById(R.id.workoutDialog_button);
            ImageButton closeButton = dialog.getWindow().findViewById(R.id.dialogCloseButton);

            icon.setImageResource(list.get(index).icon);
            title.setText(list.get(index).title);
            calories.setText(String.format(Locale.getDefault(),"%.2f",list.get(index).calories)+" KCal");
            description.setText(list.get(index).detailedDescription);
            setNumber.setText("Set 1");

            seconds=list.get(index).durationPerSet;
            restTime = list.get(index).duration/3 - seconds;
            timerText.setText(String.valueOf(seconds));
            button.setText("Start");

            closeButton.setOnClickListener(v -> {
                if(cTimerRunning)
                    countDownTimer.cancel();
                if(rTimerRunning)
                    restTimer.cancel();
                if(sTimerRunning)
                    setTimer.cancel();
                dialog.cancel();
            });

            button.setOnClickListener(v -> {
                if(button.getText().equals("Start")) {
                    button.setText("Reset");
                    button.setBackgroundColor(Color.RED);
                    final int[] i = {0};
                    setTimer = new CountDownTimer((list.get(index).duration)*1000L,(seconds+10)*1000L) {
                        @Override
                        public void onTick(long l) {
                            sTimerRunning = true;
                            i[0]++;
                            setNumber.setText("Set "+ i[0]);
                            seconds=list.get(index).durationPerSet;
                            timerText.setText(String.valueOf(seconds));

                            countDownTimer = new CountDownTimer(seconds*1000L,1000L){
                                @Override
                                public void onTick(long l) {
                                    cTimerRunning = true;
                                    timerText.setText(String.valueOf(l/1000));
                                }

                                @Override
                                public void onFinish() {
                                    cTimerRunning = false;
                                    if(i[0] == 3)
                                        setNumber.setText("Well Done!!");
                                    else
                                        setNumber.setText("Rest");

                                    seconds=restTime;
                                    timerText.setText(String.valueOf(seconds));
                                    restTimer = new CountDownTimer(restTime*1000L,1000L) {
                                        @Override
                                        public void onTick(long l) {
                                            rTimerRunning = true;
                                            timerText.setText(String.valueOf(l/1000));
                                        }

                                        @Override
                                        public void onFinish() {
                                            rTimerRunning = false;
                                        }
                                    }.start();
                                }
                            }.start();
                        }

                        @Override
                        public void onFinish() {
                            sTimerRunning = false;

                            if(sharedPreferences.getString(list.get(index).title,"").equals(todayDate)){
                                Toast.makeText(context, "Workout finished. Calories already awarded today.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(list.get(index).title,todayDate);
                                editor.apply();
                                databaseHelper.storeCalories(list.get(index).calories);
                                Toast.makeText(context, "Workout finished. Calories awarded.", Toast.LENGTH_SHORT).show();
                                holder.completedIcon.setVisibility(View.VISIBLE);
                                fitnessFragment.refreshFragment();
                            }


                            dialog.dismiss();
                        }
                    }.start();
                }
                else{
                    if(cTimerRunning)
                        countDownTimer.cancel();
                    if(rTimerRunning)
                        restTimer.cancel();
                    if(sTimerRunning)
                        setTimer.cancel();
                    button.setText("Start");
                    button.setBackgroundColor(Color.GREEN);
                    setNumber.setText("Set 1");
                    seconds=list.get(index).durationPerSet;
                    timerText.setText(String.valueOf(seconds));
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}