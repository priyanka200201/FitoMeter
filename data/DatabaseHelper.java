package com.example.fitometer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "StepsDatabase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS DailySteps(date TEXT PRIMARY KEY,stepCount INTEGER,distance REAL)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS UserInfo(gender TEXT,age INTEGER,height INTEGER,weight REAL,stepGoal INTEGER,strideLength REAL)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS CaloriesBurnt(date TEXT PRIMARY KEY,calories REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    Cursor cursor;
    public void updateStepCount(){
        int todayStepCount=getStepsForDaysBefore(0)+1;

        UserInfo userInfo = getUserInfo();
        if(todayStepCount>=getUserInfo().getStepGoal()){
            userInfo.setStepGoal(userInfo.getStepGoal()+500);
            storeUserInfo(userInfo);
        }
        double strideLength=userInfo.getStrideLength();
        double todayDistance=(strideLength*todayStepCount)/100000.0;
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH,1);
        String todayDate=calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR);
        try{
            SQLiteDatabase db=getWritableDatabase();
            ContentValues contentValues=new ContentValues();
            contentValues.put("date", todayDate);
            contentValues.put("stepCount", todayStepCount);
            contentValues.put("distance",todayDistance);
            db.replace("DailySteps",null,contentValues);
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateCalories(int stepsToCalculateCalories, double seconds,double BMR){
        double stepsPerHour =(stepsToCalculateCalories/seconds)*3600;
        double strideLength=getUserInfo().getStrideLength();
        double stepsPerMile=160934.4/strideLength;
        double walkingSpeed=stepsPerHour/stepsPerMile;
        double MET;
        if(walkingSpeed <2.0)
            MET=2.0;
        else if(walkingSpeed <2.5)
            MET=2.8;
        else if(walkingSpeed <3.0)
            MET=3.0;
        else if(walkingSpeed <3.5)
            MET=3.5;
        else if(walkingSpeed <4.0)
            MET=4.3;
        else if(walkingSpeed <4.5)
            MET=5;
        else if(walkingSpeed <5.0)
            MET=7;
        else
            MET=8.3;

        double calories=(BMR*MET*seconds)/86400.0;

        storeCalories(calories);
    }

    public void storeCalories(double calories){
        double todayCalories=getCaloriesForDaysBefore(0)+calories;
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH,1);
        String todayDate=calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR);
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("date", todayDate);
            contentValues.put("calories", todayCalories);
            db.replace("CaloriesBurnt", null, contentValues);
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void storeUserInfo(UserInfo userInfo) {
        try {
            SQLiteDatabase db=getWritableDatabase();
            ContentValues c=new ContentValues();
            c.put("gender",userInfo.getGender());
            c.put("age",userInfo.getAge());
            c.put("height",userInfo.getHeightCms());
            c.put("weight",userInfo.getWeightKgs());
            c.put("stepGoal",userInfo.getStepGoal());
            c.put("strideLength",userInfo.getStrideLength());

            cursor=db.rawQuery("SELECT * from UserInfo",null);
            if(cursor.getCount()>0)
                db.update("UserInfo",c,null,null);
            else
                db.insert("UserInfo",null,c);
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public UserInfo getUserInfo(){
        UserInfo userInfo = null;

        try{
            SQLiteDatabase db=getReadableDatabase();
            cursor=db.rawQuery("SELECT * FROM UserInfo",null);
            if(cursor.moveToFirst()){
                userInfo=new UserInfo(
                        cursor.getString(cursor.getColumnIndexOrThrow("gender")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("age")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("height")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("weight")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("stepGoal")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("strideLength")));
            }
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(userInfo!=null)
            return userInfo;
        else
            return new UserInfo("",0,0,0.0,0,0.0);
    }
    public int getStepsForDaysBefore(int i){
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH,1);
        calendar.add(Calendar.DATE,-i);
        String date=calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR);
        try {
            SQLiteDatabase db=getReadableDatabase();
            cursor= db.rawQuery("SELECT stepCount FROM DailySteps WHERE date=?", new String[]{date});
            if(cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndexOrThrow("stepCount"));
            }
            db.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public float getDistanceForDaysBefore(int i){
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH,1);
        calendar.add(Calendar.DATE,-i);
        String date=calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR);
        try {
            SQLiteDatabase db=getReadableDatabase();
            cursor= db.rawQuery("SELECT distance FROM DailySteps WHERE date=?", new String[]{date});
            if(cursor.moveToFirst()) {
                return cursor.getFloat(cursor.getColumnIndexOrThrow("distance"));
            }
            db.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public float getCaloriesForDaysBefore(int i){
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH,1);
        calendar.add(Calendar.DATE,-i);
        String todayDate=calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR);
        try {
            SQLiteDatabase db=getReadableDatabase();
            cursor= db.rawQuery("SELECT calories FROM CaloriesBurnt WHERE date=?", new String[]{todayDate});
            if(cursor.moveToFirst()) {
                return cursor.getFloat(cursor.getColumnIndexOrThrow("calories"));
            }
            db.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int getTotalSteps(){
        int totalSteps=0;
        try{
            SQLiteDatabase db=getReadableDatabase();
            cursor=db.rawQuery("SELECT stepCount FROM DailySteps",null);
            if(cursor.moveToFirst()){
                do{
                    totalSteps+=cursor.getInt(cursor.getColumnIndexOrThrow("stepCount"));
                }while(cursor.moveToNext());
            }
            db.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return totalSteps;
    }
    public float getTotalDistance(){
        float totalDistance =0;
        try{
            SQLiteDatabase db=getReadableDatabase();
            cursor=db.rawQuery("SELECT distance FROM DailySteps",null);
            if(cursor.moveToFirst()){
                do{
                    totalDistance +=cursor.getFloat(cursor.getColumnIndexOrThrow("distance"));
                }while(cursor.moveToNext());
            }
            db.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return totalDistance;
    }
    public float getTotalCalories(){
        float totalCalories=0;
        try{
            SQLiteDatabase db=getReadableDatabase();
            cursor=db.rawQuery("SELECT calories FROM CaloriesBurnt",null);
            if(cursor.moveToFirst()){
                do{
                    totalCalories +=cursor.getFloat(cursor.getColumnIndexOrThrow("calories"));
                }while(cursor.moveToNext());
            }
            db.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return totalCalories;
    }

    public List<DailyStepsData> extractAllSteps(){
        List<DailyStepsData> list = new ArrayList<>();
        String date;int steps;double distance;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.rawQuery("Select * from DailySteps",null);
            if(cursor.moveToFirst()){
                do{
                    date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                    steps = cursor.getInt(cursor.getColumnIndexOrThrow("stepCount"));
                    distance = cursor.getDouble(cursor.getColumnIndexOrThrow("distance"));
                    list.add(new DailyStepsData(date,steps,distance));
                }while(cursor.moveToNext());
                db.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public List<DailyCaloriesData> extractAllCalories(){
        List<DailyCaloriesData> list = new ArrayList<>();
        String date;double calories;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.rawQuery("Select * from CaloriesBurnt",null);
            if(cursor.moveToFirst()){
                do{
                    date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                    calories = cursor.getDouble(cursor.getColumnIndexOrThrow("calories"));
                    list.add(new DailyCaloriesData(date,calories));
                }while(cursor.moveToNext());
                db.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public void restoreStepsData(List<DailyStepsData> list){
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("DELETE FROM DailySteps");

            for (DailyStepsData i : list) {
                ContentValues c = new ContentValues();
                c.put("date",i.date);
                c.put("stepCount",i.steps);
                c.put("distance",i.distance);
                db.insert("DailySteps",null,c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void restoreCaloriesData(List<DailyCaloriesData> list){
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("DELETE FROM CaloriesBurnt");

            for (DailyCaloriesData i : list) {
                ContentValues c = new ContentValues();
                c.put("date",i.date);
                c.put("calories",i.calories);
                db.insert("CaloriesBurnt",null,c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
