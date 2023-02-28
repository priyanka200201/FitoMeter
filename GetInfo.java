package com.example.fitometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fitometer.data.DatabaseHelper;
import com.example.fitometer.data.UserInfo;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;

public class GetInfo extends AppCompatActivity {
    RadioGroup gender;
    Slider age_slider;
    NumberPicker height1,height2,height_type,weight1,weight2,weight_type;
    String[] heightType,weightType;
    ArrayList<Integer> stepGoals;
    TextView feetSymbol,inchSymbol;
    EditText age;
    Spinner step_goal;
    String user_gender="Male";
    int user_age=21,feet=5,inches=5,cm=165,goal=0;
    double kgs=60.0,lbs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_info);

        checkIfOpenedForEdit();

        manageGender();
        manageAge();
        manageHeight();
        manageWeight();
        manageStepGoal();

    }

    private void manageGender() {
        gender=findViewById(R.id.gender);

        if(user_gender.equals("Male"))
            gender.check(R.id.male);
        else
            gender.check(R.id.female);

        gender.setOnCheckedChangeListener((radioGroup, i) -> user_gender=((RadioButton)findViewById(i)).getText().toString());
    }

    private void manageAge() {
        age=findViewById(R.id.age);
        age.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
        age_slider=findViewById(R.id.age_slider);

        age.setText(String.valueOf(user_age));
        age_slider.setValue(user_age);

        age_slider.addOnChangeListener((slider, value, fromUser) -> {
            user_age=(int)value;
            age.setText(String.valueOf(user_age));
        });

        age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!age.getText().toString().equals("") && age.hasFocus()) {
                    int n = Integer.parseInt(age.getText().toString());
                    if (n >= 13 && n <= 99) {
                        age.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                        age_slider.setValue(n);
                    }
                    else {
                        age.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_error_24, 0);
                        age.setCompoundDrawablePadding(-28);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void manageHeight() {
        height1=findViewById(R.id.height1);
        height2=findViewById(R.id.height2);
        height_type=findViewById(R.id.height_type);
        feetSymbol=findViewById(R.id.feetSymbol);
        inchSymbol=findViewById(R.id.inchSymbol);
        height1.setMinValue(3);
        height1.setMaxValue(8);
        height1.setValue(feet);
        height1.setOnValueChangedListener((numberPicker, i, i1) -> {
            feet=i1;
            cm=(int) Math.round((12*feet+inches)*2.54);
        });
        height2.setMinValue(0);
        height2.setMaxValue(11);
        height2.setValue(inches);
        height2.setOnValueChangedListener((numberPicker, i, i1) -> {
            if(height_type.getValue()==1) {
                inches = i1;
                cm = (int) Math.round((12 * feet + inches) * 2.54);
            }
            else
                cm=i1;
        });
        height_type.setMinValue(0);
        height_type.setMaxValue(1);
        height_type.setValue(1);
        heightType=getResources().getStringArray(R.array.heightType);
        height_type.setDisplayedValues(heightType);

        height_type.setOnValueChangedListener((numberPicker, i, i1) -> {
            if(i1==0) {
                height1.setVisibility(NumberPicker.INVISIBLE);
                feetSymbol.setVisibility(TextView.INVISIBLE);
                inchSymbol.setVisibility(TextView.INVISIBLE);
                height2.setMinValue(90);
                height2.setMaxValue(270);
                height2.setValue(cm);
            }
            else {
                height1.setVisibility(NumberPicker.VISIBLE);
                feetSymbol.setVisibility(TextView.VISIBLE);
                inchSymbol.setVisibility(TextView.VISIBLE);
                height2.setMinValue(0);
                height2.setMaxValue(11);
                feet=(int) Math.floor(cm/(2.54*12));
                height1.setValue(feet);
                inches=(int)Math.round((cm/(2.54*12)-feet)*12);
                height2.setValue(inches);

            }
        });
    }

    private void manageWeight() {
        weight1=findViewById(R.id.weight1);
        weight2=findViewById(R.id.weight2);
        weight_type=findViewById(R.id.weight_type);
        weight1.setMinValue(30);
        weight1.setMaxValue(200);
        weight1.setValue((int)Math.floor(kgs));
        weight1.setOnValueChangedListener((numberPicker, i, i1) -> {
            if(weight_type.getValue()==0)
                kgs+=(i1-i);
            else
                lbs+=(i1-i);
        });

        weight2.setMinValue(0);
        weight2.setMaxValue(9);
        weight2.setValue((int)((kgs-Math.floor(kgs))*10));
        weight2.setOnValueChangedListener((numberPicker, i, i1) -> {
            if(weight_type.getValue()==0)
                kgs=Math.floor(kgs)+(0.1*i1);
            else
                lbs=Math.floor(lbs)+(0.1*i1);
        });

        weight_type.setMinValue(0);
        weight_type.setMaxValue(1);
        weightType=getResources().getStringArray(R.array.weightType);
        weight_type.setDisplayedValues(weightType);
        weight_type.setOnValueChangedListener((numberPicker, i, i1) -> {
            if(i1==1){
                weight1.setMinValue(66);
                weight1.setMaxValue(441);
                lbs=Math.round(2.205*kgs*10.0)/10.0;    //1 decimal place
                weight1.setValue((int)Math.floor(lbs));
                weight2.setValue((int)((lbs-Math.floor(lbs))*10));
            }
            else{
                weight1.setMinValue(30);
                weight1.setMaxValue(200);
                kgs=Math.round((lbs/2.205)*10.0)/10.0;  //1 decimal place
                weight1.setValue((int)Math.floor(kgs));
                weight2.setValue((int)((kgs-Math.floor(kgs))*10));
            }
        });
    }

    private void manageStepGoal() {
        step_goal=findViewById(R.id.step_goal);
        stepGoals=new ArrayList<>();
        for (int i = 500; i <= 25000 ; i+=500)
            stepGoals.add(i);

        ArrayAdapter<Integer> adapter=new ArrayAdapter<>(this, R.layout.style_spinner,stepGoals);
        adapter.setDropDownViewResource(R.layout.style_spinner);
        step_goal.setAdapter(adapter);

        step_goal.setSelection((goal/500)-1);

        step_goal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                goal= (int) adapterView.getItemAtPosition(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void checkIfOpenedForEdit() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("editingProfile",false)){
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            UserInfo userInfo = databaseHelper.getUserInfo();

            user_gender = userInfo.getGender();
            user_age = userInfo.getAge();
            cm = userInfo.getHeightCms();
            feet=(int) Math.floor(cm/(2.54*12));
            inches=(int)Math.round((cm/(2.54*12)-feet)*12);
            kgs = userInfo.getWeightKgs();
            goal = userInfo.getStepGoal();

            ImageButton editProfileBackButton = findViewById(R.id.editProfileBackButton);
            LinearLayout editProfileLayout = findViewById(R.id.editProfileExtraLayout);
            editProfileLayout.setVisibility(View.VISIBLE);

            editProfileBackButton.setOnClickListener(v -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("editingProfile",false);
                editor.apply();
                finish();
            });
        }
    }

    public void saveInfo(View view){
        double strideLength=0;
        if(user_gender.equals("Male"))
            strideLength=0.415*cm;
        else if(user_gender.equals("Female"))
            strideLength=0.413*cm;

        UserInfo userInfo=new UserInfo(user_gender,user_age,cm,kgs,goal, strideLength);
        new DatabaseHelper(this).storeUserInfo(userInfo);

        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("editingProfile",false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("editingProfile", false);
            editor.apply();
            finish();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}