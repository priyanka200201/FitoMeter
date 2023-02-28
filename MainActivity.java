package com.example.fitometer;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fitometer.achievements.Achievements;
import com.example.fitometer.data.DatabaseHelper;
import com.example.fitometer.data.UserInfo;
import com.example.fitometer.fitness.Fitness;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    SensorManager sensorManager;
    DatabaseHelper databaseHelper;
    Sensor stepDetector;
    HomeScreen homeScreen;
    BottomNavigationView bottomNavigationView;
    UserInfo userInfo;
    int stepCount = 0, stepGoal,stepsToCalculateCalories=0,seconds=0,currentFragment;
    Handler handler=new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seconds=0;
            if(stepsToCalculateCalories!=0) {
                databaseHelper.updateCalories(stepsToCalculateCalories, 60.0, userInfo.calculateBMR());
                if(currentFragment == R.id.homeScreen)
                    homeScreen.updateCalories();
                stepsToCalculateCalories = 0;
            }
            handler.postDelayed(runnable, 60000);
        }
    };
    Runnable count_seconds = new Runnable() {
        @Override
        public void run() {
            seconds++;
            handler.postDelayed(count_seconds,1000);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            SharedPreferences sharedPreferences=getSharedPreferences("myPrefs",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.remove("running").apply();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            currentFragment = item.getItemId();
            if (currentFragment == R.id.homeScreen) {
                startFragment(homeScreen);
                return true;
            }
            if (currentFragment == R.id.achievements) {
                startFragment(new Achievements());
                return true;
            }
            else if (currentFragment == R.id.fitness) {
                startFragment(new Fitness());
                return true;
            } else if (currentFragment == R.id.more) {
                startFragment(new More());
                return true;
            }
            return false;
        });

        homeScreen=new HomeScreen();
        bottomNavigationView.setSaveEnabled(false);
        bottomNavigationView.setSelectedItemId(R.id.homeScreen);

    }
    public void startFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView,fragment);
        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        SharedPreferences sharedPreferences= getSharedPreferences("myPrefs", MODE_PRIVATE);
        if(sharedPreferences.getBoolean("running",false)) {
            databaseHelper.updateStepCount();
            stepsToCalculateCalories++;
            if(currentFragment == R.id.homeScreen)
                homeScreen.updateStepsUI();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //nothing happens here
    }

    public void startCalorieCalculation(){
        handler.postDelayed(count_seconds,1000);
        handler.postDelayed(runnable,60000);
    }

    public void stopCalorieCalculation(){
        if(stepsToCalculateCalories!=0) {
            databaseHelper.updateCalories(stepsToCalculateCalories, seconds, userInfo.calculateBMR());
            if(currentFragment == R.id.homeScreen)
                homeScreen.updateCalories();
            stepsToCalculateCalories = 0;
        }
        seconds=0;
        handler.removeCallbacks(count_seconds);
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences=getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        if(sharedPreferences.getBoolean("running",false)) {
            startService(new Intent(this,StepDetectorService.class).setAction("STOP"));
        }
        else{
            editor.putBoolean("running",true);
            editor.apply();
        }
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        databaseHelper=new DatabaseHelper(this);
        userInfo=databaseHelper.getUserInfo();
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepDetector != null) {
            sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_FASTEST);
            stepCount = databaseHelper.getStepsForDaysBefore(0);
            stepGoal=userInfo.getStepGoal();
            startCalorieCalculation();
        }
        else
            Toast.makeText(this, "Step Detector sensor is absent.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        SharedPreferences sharedPreferences=getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        if(sharedPreferences.getBoolean("running",false)) {
            startForegroundService(new Intent(this,StepDetectorService.class));
            editor.apply();
        }
        stopCalorieCalculation();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(bottomNavigationView.getSelectedItemId()==R.id.homeScreen)
            super.onBackPressed();
        else{
            bottomNavigationView.setSelectedItemId(R.id.homeScreen);
        }
    }
}