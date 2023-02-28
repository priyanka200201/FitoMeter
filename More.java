package com.example.fitometer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.fitometer.data.DailyCaloriesData;
import com.example.fitometer.data.DailyStepsData;
import com.example.fitometer.data.DatabaseHelper;
import com.example.fitometer.data.UserInfo;
import com.example.fitometer.help.Help;
import com.example.fitometer.history.History;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class More extends Fragment {

    View view;
    CardView profileCardView,historyCardView,backupAndRestoreCardView,helpCardView,feedbackCardView;
    ImageButton profileBackButton,profileEditButton,feedbackSendButton;
    ImageView profilePicture,profileExpandIcon;
    LinearLayout profileInfo,backupAndRestoreLayout;
    ConstraintLayout feedbackLayout;
    TextView profileName,profileGender,profileAge,profileHeight,profileWeight,profileStrideLength,profileBMI,profileBMR,profileStepGoal,
            backup,restore,lastBackupText, feedbackText;
    Spinner profileHeightType,profileWeightType;
    Button signInOutButton;
    EditText feedbackSubject,feedbackDescription;
    TextInputLayout feedbackSubjectLayout,feedbackDescriptionLayout;

    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    List<String> restoredData;

    DatabaseHelper databaseHelper;
    UserInfo userInfo;

    String cardViewUsedForAuthentication = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_more, container, false);

        manageProfile();

        historyCardView = view.findViewById(R.id.history_card_view);

        historyCardView.setOnClickListener(v -> {
            History history = new History();
            getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,history)
                    .addToBackStack(history.getTag()).
                    setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right).commit();
        });

        backupAndRestoreCardView = view.findViewById(R.id.backupAndRestore_card_view);
        backupAndRestoreLayout = view.findViewById(R.id.backupAndRestore_layout);

        backupAndRestoreCardView.setOnClickListener(v ->{
            if(backupAndRestoreLayout.getVisibility()==View.VISIBLE)
                backupAndRestoreLayout.setVisibility(View.GONE);
            else
                backupAndRestore();
        });

        helpCardView = view.findViewById(R.id.help_card_view);
        helpCardView.setOnClickListener(v -> {
            Help help = new Help();
            getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,help)
                    .addToBackStack(help.getTag()).
                    setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right).commit();
        });

        manageFeedback();

        signInOutButton = view.findViewById(R.id.signInOutButton);


        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
            signInOutButton.setText(getString(R.string.sign_out));
        }
        signInOutButton.setOnClickListener(v -> {
            if(signInOutButton.getText().equals(getString(R.string.sign_in))){
                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.webSignInClient)).requestEmail().build();
                googleSignInClient = GoogleSignIn.getClient(requireContext(),googleSignInOptions);

                cardViewUsedForAuthentication = "SignInButton";

                Intent signInIntent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(signInIntent);
            }
            else {
                firebaseAuth.signOut();
                googleSignInClient = GoogleSignIn.getClient(requireContext(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());
                googleSignInClient.signOut().addOnCompleteListener(task -> {
                    Toast.makeText(requireContext(), "Signed out successfully!", Toast.LENGTH_SHORT).show();
                    if(backupAndRestoreLayout.getVisibility()==View.VISIBLE)
                            backupAndRestoreLayout.setVisibility(View.GONE);
                    if(feedbackLayout.getVisibility()==View.VISIBLE)
                        feedbackLayout.setVisibility(View.GONE);

                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("displayName").apply();
                    profileName.setText(getString(R.string.guest_user));
                    signInOutButton.setText(getString(R.string.sign_in));
                });
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setProfileInfo();
    }

    private void manageProfile() {
        profileCardView = view.findViewById(R.id.profile_card_view);
        profileBackButton = view.findViewById(R.id.profile_back_button);
        profileEditButton = view.findViewById(R.id.profile_edit_button);
        profilePicture = view.findViewById(R.id.profile_picture);
        profileExpandIcon = view.findViewById(R.id.profileExpandIcon);
        profileInfo = view.findViewById(R.id.profile_info);

        profileName = view.findViewById(R.id.profileName);
        profileGender = view.findViewById(R.id.profile_gender);
        profileAge = view.findViewById(R.id.profile_age);
        profileStrideLength = view.findViewById(R.id.profile_strideLength);
        profileBMI = view.findViewById(R.id.profile_bmi);
        profileBMR = view.findViewById(R.id.profile_bmr);
        profileHeight = view.findViewById(R.id.profile_height);
        profileWeight = view.findViewById(R.id.profile_weight);
        profileStepGoal = view.findViewById(R.id.profile_stepGoal);

        profileHeightType = view.findViewById(R.id.heightType_spinner);
        profileWeightType = view.findViewById(R.id.weightType_spinner);


        profileName.setText(requireContext().getSharedPreferences("myPrefs",Context.MODE_PRIVATE).
                getString("displayName","Guest User"));

        profileCardView.setOnClickListener(v -> {
            if(profileInfo.getVisibility() == View.GONE){
                profileInfo.setVisibility(View.VISIBLE);
                profileBackButton.setVisibility(View.VISIBLE);
                profileEditButton.setVisibility(View.VISIBLE);
                profileExpandIcon.setVisibility(View.GONE);
                setProfileInfo();
            }
        });

        profileBackButton.setOnClickListener(v -> {
            profileInfo.setVisibility(View.GONE);
            profileBackButton.setVisibility(View.GONE);
            profileEditButton.setVisibility(View.GONE);
            profileExpandIcon.setVisibility(View.VISIBLE);
        });

        profileEditButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("editingProfile",true);
            editor.apply();
            Intent intent = new Intent(requireContext(),GetInfo.class);
            startActivity(intent);
        });


    }

    @SuppressLint("SetTextI18n")
    private void setProfileInfo() {
        databaseHelper = new DatabaseHelper(requireContext());
        userInfo = databaseHelper.getUserInfo();

        String gender = userInfo.getGender();
        int age = userInfo.getAge();
        int heightCms = userInfo.getHeightCms();
        double weightKgs = userInfo.getWeightKgs();
        double strideLength = userInfo.getStrideLength();
        double bmi = userInfo.calculateBMI();
        double bmr = userInfo.calculateBMR();
        int stepGoal = userInfo.getStepGoal();

        if(gender.equals("Male"))
            profilePicture.setImageResource(R.drawable.male_icon);
        else
            profilePicture.setImageResource(R.drawable.female_icon);
        profileGender.setText(gender);
        profileAge.setText(String.valueOf(age));
        profileHeight.setText(String.valueOf(heightCms));
        profileWeight.setText(String.valueOf(weightKgs));
        profileStrideLength.setText(String.format(Locale.getDefault(),"%.3f",strideLength)+" cm");
        profileBMI.setText(String.format(Locale.getDefault(),"%.2f",bmi));
        profileBMR.setText(String.format(Locale.getDefault(),"%.2f",bmr));
        profileStepGoal.setText(String.valueOf(stepGoal));

        profileHeightType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
                if(i==1) {
                    int feet = (int) Math.floor(heightCms / (2.54 * 12));
                    int inches = (int) Math.round((heightCms/ (2.54 * 12) - feet) * 12);
                    profileHeight.setText(feet+ "'" +inches+"\"");
                }
                else
                    profileHeight.setText(String.valueOf(heightCms));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        profileWeightType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
                double kgs = userInfo.getWeightKgs();
                if(i == 1) {
                    double weightLbs = Math.round(2.205 * kgs * 10.0) / 10.0;
                    profileWeight.setText(String.valueOf(weightLbs));
                }
                else
                    profileWeight.setText(String.valueOf(weightKgs));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void backupAndRestore() {
        if(!isOnline()){
            Toast.makeText(requireContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(firebaseAuth.getCurrentUser()==null){
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.webSignInClient)).requestEmail().build();
            googleSignInClient = GoogleSignIn.getClient(requireContext(),googleSignInOptions);

            cardViewUsedForAuthentication = "BackupAndRestore";

            Intent signInIntent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(signInIntent);
        }
        else
            expandBackupAndRestoreView();

    }
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == Activity.RESULT_OK){
            Intent data = result.getData();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }catch(ApiException e){
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    });

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);

        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(requireActivity(), task -> {
            if(task.isSuccessful()){
                String name = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
                Toast.makeText(requireContext(), "Welcome "+name+"!", Toast.LENGTH_SHORT).show();
                profileName.setText(name);
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("displayName",name);
                editor.apply();
                profileName.setText(requireContext().getSharedPreferences("myPrefs",Context.MODE_PRIVATE).
                        getString("displayName","Guest User"));
                if(cardViewUsedForAuthentication.equals("BackupAndRestore"))
                    expandBackupAndRestoreView();
                else if(cardViewUsedForAuthentication.equals("Feedback"))
                    expandFeedbackView();
                signInOutButton.setText(getString(R.string.sign_out));
            }
            else
                Toast.makeText(requireContext(), "Authentication failed!", Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("SetTextI18n")
    private void expandBackupAndRestoreView() {
        backupAndRestoreLayout.setVisibility(View.VISIBLE);

        backup = view.findViewById(R.id.backup);
        restore = view.findViewById(R.id.restore);
        lastBackupText = view.findViewById(R.id.lastBackupText);

        restoredData = new ArrayList<>();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        db.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    restoredData.add(Objects.requireNonNull(snapshot.getValue()).toString());
                }
                if(!restoredData.isEmpty())
                    lastBackupText.setText(getString(R.string.last_backup)+" "+restoredData.get(1));
                else
                    lastBackupText.setText(getString(R.string.last_backup)+" Never");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        backup.setOnClickListener(v -> {
            if(isOnline()){
                List<DailyStepsData> stepsDataList = databaseHelper.extractAllSteps();
                List<DailyCaloriesData> caloriesDataList = databaseHelper.extractAllCalories();
                Gson gson = new Gson();
                String stepsDataJson = gson.toJson(stepsDataList);
                String caloriesDataJson = gson.toJson(caloriesDataList);

                db.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("CaloriesData").setValue(caloriesDataJson);
                db.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("LastBackup").setValue(String.valueOf(Calendar.getInstance().getTime()));
                db.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("StepsData").setValue(stepsDataJson);
                db.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("UserInfo").setValue(userInfo);
                Toast.makeText(requireContext(), "Backup Successful!", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(requireContext(), "No Internet Connection!!", Toast.LENGTH_SHORT).show();

            backupAndRestoreLayout.setVisibility(View.GONE);
        });

        restore.setOnClickListener(v -> {
            if(isOnline()) {
                if (!restoredData.isEmpty()) {
                    Gson gson = new Gson();
                    List<DailyStepsData> stepsDataList = gson.fromJson(restoredData.get(2),
                            new TypeToken<List<DailyStepsData>>() {}.getType());
                    List<DailyCaloriesData> caloriesDataList = gson.fromJson(restoredData.get(0),
                            new TypeToken<List<DailyCaloriesData>>() {}.getType());
                    userInfo = gson.fromJson(restoredData.get(3), UserInfo.class);

                    Toast.makeText(requireContext(), "Data restored successfully!", Toast.LENGTH_SHORT).show();
                    databaseHelper.restoreStepsData(stepsDataList);
                    databaseHelper.restoreCaloriesData(caloriesDataList);
                    databaseHelper.storeUserInfo(userInfo);
                }
                else
                    Toast.makeText(requireContext(), "No active backups found!!", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(requireContext(), "No Internet Connection!!", Toast.LENGTH_SHORT).show();

            backupAndRestoreLayout.setVisibility(View.GONE);
        });

    }
    public boolean isOnline(){
        Runtime runtime = Runtime.getRuntime();
        try{
            Process process = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = process.waitFor();
            return exitValue == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void manageFeedback(){
        feedbackCardView = view.findViewById(R.id.feedback_card_view);
        feedbackText = view.findViewById(R.id.feedback_text);
        feedbackLayout = view.findViewById(R.id.feedbackLayout);
        feedbackCardView.setOnClickListener(v -> {
            if(feedbackLayout.getVisibility() == View.VISIBLE) {
                feedbackLayout.setVisibility(View.GONE);
                feedbackText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_feedback_24,0,R.drawable.ic_baseline_expand_more_24,0);
            }
            else{
                if(!isOnline()){
                    Toast.makeText(requireContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(firebaseAuth.getCurrentUser()==null){
                    GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.webSignInClient)).requestEmail().build();
                    googleSignInClient = GoogleSignIn.getClient(requireContext(),googleSignInOptions);

                    cardViewUsedForAuthentication = "Feedback";

                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    activityResultLauncher.launch(signInIntent);
                }
                else
                    expandFeedbackView();

            }
            });
    }

    private void expandFeedbackView() {
        feedbackLayout.setVisibility(View.VISIBLE);
        feedbackText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_feedback_24,0,R.drawable.ic_baseline_expand_less_24,0);

        feedbackSubject = view.findViewById(R.id.feedback_subject);
        feedbackDescription = view.findViewById(R.id.feedback_description);
        feedbackSubjectLayout = view.findViewById(R.id.feedback_subject_textInputLayout);
        feedbackDescriptionLayout = view.findViewById(R.id.feedback_description_textInputLayout);
        feedbackSendButton = view.findViewById(R.id.feedback_send_button);

        feedbackSubject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!feedbackSubject.getText().toString().equals("") && feedbackSubjectLayout.isErrorEnabled())
                    feedbackSubjectLayout.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        feedbackDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!feedbackDescription.getText().toString().equals("") && feedbackDescriptionLayout.isErrorEnabled())
                    feedbackDescriptionLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        feedbackSendButton.setOnClickListener(v -> {
            String subject = feedbackSubject.getText().toString();
            String description = feedbackDescription.getText().toString();
            if(subject.equals("")) {
                feedbackSubjectLayout.setError("Subject cannot be empty!!");
                feedbackSubjectLayout.requestFocus();
            }
            else if(description.equals("")) {
                feedbackDescriptionLayout.setError("Description cannot be empty!!");
                feedbackDescriptionLayout.requestFocus();
            }
            else{
                String user = "User: "+ Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
                subject = "Fitometer:  "+subject;
                description = user + "\n\nFeedback:\n\n" + description;
                Intent sendMailIntent = new Intent(Intent.ACTION_SENDTO);
                sendMailIntent.setData(Uri.parse("mailto:"));
                sendMailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"rohan30072002@gmail.com"});
                sendMailIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
                sendMailIntent.putExtra(Intent.EXTRA_TEXT,description);
                try{
                    startActivity(sendMailIntent);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(requireContext(), "Couldn't send email!", Toast.LENGTH_SHORT).show();
                }
            }


        });


    }

}