package com.example.fitometer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.fitometer.data.DatabaseHelper;
import com.example.fitometer.data.UserInfo;

public class StepDetectorService extends Service implements SensorEventListener {

    SensorManager sensorManager;
    DatabaseHelper databaseHelper;
    UserInfo userInfo;
    Sensor stepDetector;
    NotificationChannel channel;
    NotificationManager notificationManager;
    Notification notification;
    Intent notificationIntent;
    PendingIntent pendingIntent;

    int stepCount = 0, stepGoal,stepsToCalculateCalories=0,seconds=0;
    String action = "";
    final String CHANNEL_ID = "Step Detector Service ID";

    Handler handler=new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seconds=0;
            if(stepsToCalculateCalories!=0) {
                databaseHelper.updateCalories(stepsToCalculateCalories, 60.0, userInfo.calculateBMR());
                stepsToCalculateCalories = 0;
                reRegisterListener();
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
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null){
            action = intent.getAction();
            if(action == "STOP")
                stopForegroundService();
        }
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        databaseHelper=new DatabaseHelper(this);
        userInfo = databaseHelper.getUserInfo();
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_FASTEST);
        stepCount = databaseHelper.getStepsForDaysBefore(0);
        stepGoal=userInfo.getStepGoal();
        startCalorieCalculation();

        channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

        startForeground(1,getNotification());

        return START_STICKY;
    }

    public Notification getNotification(){
        stepCount = databaseHelper.getStepsForDaysBefore(0);
        stepGoal = databaseHelper.getUserInfo().getStepGoal();
        String text = stepCount +"/"+ stepGoal;
        notificationIntent = new Intent(this,SplashScreen.class);
        pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_IMMUTABLE);
        notification = new Notification.Builder(this,CHANNEL_ID)
                .setContentTitle("Today's Steps")
                .setContentText(text)
                .setProgress(stepGoal,stepCount,false)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        return notification;
    }

    public void startCalorieCalculation(){
        handler.postDelayed(count_seconds,1000);
        handler.postDelayed(runnable,60000);
    }

    public void stopCalorieCalculation(){
        if(stepsToCalculateCalories!=0) {
            databaseHelper.updateCalories(stepsToCalculateCalories, seconds, userInfo.calculateBMR());
            stepsToCalculateCalories = 0;
        }
        seconds=0;
        handler.removeCallbacks(count_seconds);
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        databaseHelper.updateStepCount();
        stepsToCalculateCalories++;
        notificationManager.notify(1,getNotification());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void stopForegroundService(){
        stopForeground(true);
        stopSelf();
    }

    public void reRegisterListener(){
        sensorManager.unregisterListener(this);
        sensorManager.registerListener(this,stepDetector,SensorManager.SENSOR_DELAY_FASTEST);
    }
    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        stopCalorieCalculation();
        super.onDestroy();
    }
}
