package com.example.fitometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.fitometer.data.DatabaseHelper;

public class SplashScreen extends AppCompatActivity {
    Intent intent;
    TextView app_name,tagline;
    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            DatabaseHelper databaseHelper=new DatabaseHelper(getApplicationContext());
            if(databaseHelper.getUserInfo().getStepGoal()==0)
                intent=new Intent(getApplicationContext(),GetInfo.class);
            else
                intent=new Intent(getApplicationContext(),MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        app_name = findViewById(R.id.app_name);
        tagline=findViewById(R.id.tagline);
    }

    @Override
    protected void onResume() {
        super.onResume();
        app_name.animate().translationY(1000).setDuration(1000).setStartDelay(1300);
        tagline.animate().translationY(1000).setDuration(1000).setStartDelay(1500);

        handler.postDelayed(runnable,2000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        app_name.clearAnimation();
        tagline.clearAnimation();
        app_name.animate().translationX(0).translationY(0);
        tagline.animate().translationX(0).translationY(0);
    }
}
