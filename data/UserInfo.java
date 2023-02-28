package com.example.fitometer.data;

public class UserInfo {
    private final String gender;
    private final int age;
    private final int heightCms;
    private final double weightKgs;
    private int stepGoal;
    private final double strideLength;

    public UserInfo(String gender, int age, int heightCms, double weightKgs, int stepGoal, double strideLength) {
        this.gender = gender;
        this.age = age;
        this.heightCms = heightCms;
        this.weightKgs = weightKgs;
        this.stepGoal = stepGoal;
        this.strideLength = strideLength;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public int getHeightCms() {
        return heightCms;
    }

    public double getWeightKgs() {
        return weightKgs;
    }

    public int getStepGoal() {
        return stepGoal;
    }

    public double getStrideLength() {
        return strideLength;
    }
    //BMR: Basal Metabolic rate
    public double calculateBMR(){   //Harris-Benedict Equation
        double bmr = 0;
            if(gender.equals("Male"))
            bmr=88.362+(13.397*weightKgs)+(4.799*heightCms)-(5.677*age);
        else if(gender.equals("Female"))
            bmr=447.593+(9.247*weightKgs)+(3.098*heightCms)-(4.330*age);
        return bmr;
    }
    public double calculateBMI(){
        return weightKgs / Math.pow(heightCms/100.0,2);
    }

    public void setStepGoal(int stepGoal){
        this.stepGoal = stepGoal;
    }

}
