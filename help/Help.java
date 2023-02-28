package com.example.fitometer.help;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.fitometer.R;

import java.util.ArrayList;
import java.util.List;

public class Help extends Fragment {

    RecyclerView helpRecyclerView;
    ImageButton helpBackButton;
    View view;
    List<HelpData> list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_help, container, false);

        list = updateHelp();
        helpRecyclerView = view.findViewById(R.id.help_recycler_view);
        HelpAdapter helpAdapter = new HelpAdapter(list,requireContext());
        helpRecyclerView.setAdapter(helpAdapter);
        helpRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        helpBackButton = view.findViewById(R.id.helpBackButton);
        helpBackButton.setOnClickListener(v -> getParentFragmentManager().popBackStackImmediate());
        return view;
    }

    private List<HelpData> updateHelp() {
        List<HelpData> list = new ArrayList<>();
        list.add( new HelpData("How to use?",
                "Click PAUSE button on homepage to stop counting and reduce power consumption. " +
                        "Click RESUME button to start counting again."));
        list.add( new HelpData("Counting when shaking phone?",
                "If you shake your phone it might be counted as steps because of the built in sensor." +
                "We will keep working hard for a better user experience." ));
        list.add( new HelpData("Counting when driving?",
                "In certain driving circumstances, it may count some steps due to system restrictions, " +
                        "which depends on the conditions you are driving in and where you place your phone." +
                "If this problem occurs, you may tap the PAUSE button to pause the step counter when you are taking a long drive." ));
        list.add( new HelpData("Accuracy",
                "We use the built-in sensor in your phone to count steps, " +
                        "so the accuracy is based on how good the sensor is."));
        list.add( new HelpData("Placement Suggestion",
                "We suggest you to put it: in your hand, pocket, bag, arm band or places close to your body." +
                "Don't put it in places away from your body, places that shake too fast or places that have little movement."));
        list.add( new HelpData("Battery Saving",
                "We read your steps from the built-in sensor. No GPS tracking is used, so the battery consumption is minimal. " +
                        "You can pause the app to save the battery when you are not working."));
        list.add( new HelpData("Privacy",
                "100% private. No personal data collection. No data sharing with third parties."));
        list.add( new HelpData("BMI and BMR","We use your weight, height, age to calculate your BMI and BMR, " +
                "so please ensure that you input accurate information in your profile."));
        list.add( new HelpData("Step Goal",
                "We help you set your goal according to your achieved steps to keep you motivated. " +
                        "It is also updated automatically based on your progress."));
        list.add( new HelpData("Stride Length",
                "We calculate your stride length automatically based on your height."));
        list.add( new HelpData("Distance Travelled",
                "The distance travelled is measured by getting the product of your stride length and number of steps walked."));
        list.add( new HelpData("Calories Burnt",
                "The amount of calories you burn is dependent on your BMR and the intensity" +
                        " of the activity you're doing."));
        list.add(new HelpData("Not Counting In Background","Android may stop the step counting service from running" +
                "in the background sometimes. It is advised to use the app in the foreground as much as possible."));

        return list;

    }
}