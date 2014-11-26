package com.denyszaiats.myreactions;

import java.sql.Date;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import android.widget.*;
import com.squareup.picasso.Picasso;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TextView summaryResults;
    private TextView nameView;
    private TextView genderView;
    private TextView ageView;
    private TextView textSummaryClicks;
    private TextView textMaxClicks;
    private TextView textTitleResults;
    private ImageView imgIcon;
    private Spinner dropMenuHand;
    private Spinner dropMenuFinger;
    private Button buttonShowResults;
    private ScrollView resultsScrollView;
    private ProgressBar progressBar;

    private SharedPreferences prefs;
    private ArrayAdapter<String> adapter;
    private Context context;

    private String hand = Constants.RIGHT_HAND;
    private String finger = Constants.THUMB_FINGER;
    private String resultsMaxClicks;
    private String resultsSummaryClicks;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        summaryResults = (TextView) rootView.findViewById(R.id.textSummaryClicks);
        nameView = (TextView) rootView.findViewById(R.id.textViewName);
        genderView = (TextView) rootView.findViewById(R.id.textViewGender);
        ageView = (TextView) rootView.findViewById(R.id.textViewAge);
        imgIcon = (ImageView) rootView.findViewById(R.id.imageProfileHome);
        dropMenuHand = (Spinner) rootView.findViewById(R.id.dropMenuHand);
        dropMenuFinger = (Spinner) rootView.findViewById(R.id.dropMenuFinger);
        buttonShowResults = (Button) rootView.findViewById(R.id.buttonShowResults);
        textSummaryClicks = (TextView) rootView.findViewById(R.id.textSummaryClicksByHandAndFinger);
        textMaxClicks = (TextView) rootView.findViewById(R.id.textMaxClicksByHandAndFinger);
        textTitleResults = (TextView) rootView.findViewById(R.id.textTitleForStatisticsInScrollView);
        resultsScrollView = (ScrollView) rootView.findViewById(R.id.scrollViewHome);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressDialog);

        String[] fingers = new String[]{"Thumb", "Index", "Middle", "Ring", "Pinky"};
        setSpinnerAdapter(fingers, dropMenuFinger);

        String[] hands = new String[]{"Right", "Left"};
        setSpinnerAdapter(hands, dropMenuHand);

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String username = prefs.getString("USER_NAME", "Guest");
        String facebookId = prefs.getString("FACEBOOK_ID", "");
        String gender = prefs.getString("GENDER", "");
        String birthday = prefs.getString("BIRTHDAY", "");
        String age = "";

        if (!birthday.equals("")) {
            long birthdayDate = Date.parse(birthday);
            long todayDate = System.currentTimeMillis();
            long ageDate = (todayDate - birthdayDate) / (1000 * 60 * 60 * 24 * 365);
            age = String.valueOf(ageDate);

        }

        nameView.setText("Name: " + username);
        genderView.setText("Gender: " + gender);
        ageView.setText("Age: " + age);

        String imgUri = "";
        if (username.equals("Guest")) {
            imgIcon.setImageResource(R.drawable.ic_guest);
        } else {
            imgUri = "https://graph.facebook.com/" + facebookId
                    + "/picture?type=large";
            Picasso.with(context).load(imgUri).into(imgIcon);
        }

        String summaryClicks = prefs.getString(Constants.SUMMARY_CLICKS, "");
        if (summaryClicks.equals("")) {
            summaryResults.setText("You made no one clicks. Just do it!");
        } else {
            summaryResults.setText("You already made " + summaryClicks + " clicks");
        }

        buttonShowResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dropMenuHand.getSelectedItem().equals("Right")) {
                    hand = Constants.RIGHT_HAND;
                } else {
                    hand = Constants.LEFT_HAND;
                }
                if (dropMenuFinger.getSelectedItem().equals("Thumb")) {
                    finger = Constants.THUMB_FINGER;
                } else if (dropMenuFinger.getSelectedItem().equals("Index")) {
                    finger = Constants.INDEX_FINGER;
                } else if (dropMenuFinger.getSelectedItem().equals("Middle")) {
                    finger = Constants.MIDDLE_FINGER;
                } else if (dropMenuFinger.getSelectedItem().equals("Ring")) {
                    finger = Constants.RING_FINGER;
                } else if (dropMenuFinger.getSelectedItem().equals("Pinky")) {
                    finger = Constants.PINKY_FINGER;
                }

                resultsScrollView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                resultsMaxClicks = getMaxValueOfClicks(hand, finger).toString();
                resultsSummaryClicks = getSumValueOfClicks(hand, finger).toString();
                progressBar.setVisibility(View.INVISIBLE);
                resultsScrollView.setVisibility(View.VISIBLE);
                textTitleResults.setText(String.format("Results are showed for %s hand and %s finger:", dropMenuHand.getSelectedItem(), dropMenuFinger.getSelectedItem()));
                textSummaryClicks.setText("Summary clicks: " + resultsSummaryClicks);
                textMaxClicks.setText("Max clicks per 10 seconds: " + resultsMaxClicks);

                //TODO android graphs. Vertical - clicks, horizontal - seconds
                //http://stackoverflow.com/questions/9741300/charts-for-android

            }
        });

        return rootView;
    }

    private void setSpinnerAdapter(String[] data, Spinner spinner) {
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private Integer getMaxValueOfClicks(String hand, String finger) {
        Integer value = 0;
        String filter = hand + "-" + finger;
        LinkedList<Integer> amount = new LinkedList<Integer>();
        Map<String, ?> allEntries = prefs.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith(filter)) {
                int sum = entry.getValue().toString().split(",").length;
                amount.add(sum);
            }
        }
        if (amount.size() != 0) {
            Collections.sort(amount);
            value = amount.getFirst();
        }
        return value;
    }

    private Integer getSumValueOfClicks(String hand, String finger) {
        Integer value = 0;
        String filter = hand + "-" + finger;
        LinkedList<Integer> amount = new LinkedList<Integer>();
        Map<String, ?> allEntries = prefs.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith(filter)) {
                int sum = entry.getValue().toString().split(",").length;
                amount.add(sum);
            }
        }
        if (amount.size() != 0) {
            for (Integer i : amount) {
                value += i;
            }
        }
        return value;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
