package com.denyszaiats.myreactions;

import java.sql.Date;
import java.util.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.*;
import com.denyszaiats.myreactions.ChartView.ChartView;
import com.denyszaiats.myreactions.ChartView.LinearSeries;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.squareup.picasso.Picasso;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.SharedPreferences.Editor;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TextView summaryResults;
    private TextView nameView;
    private TextView genderView;
    private TextView ageView;
    private TextView textSummaryClicks;
    private TextView textMaxClicks;
    private TextView textMinClicks;
    private TextView textTitleResults;
    private TextView textChooseColorHighlevel;
    private TextView textChooseColorHighscore;
    private TextView textRemColorHighlevel;
    private TextView textRemColorHighscore;
    private ImageView imgIcon;
    private Spinner dropMenuHand;
    private Spinner dropMenuFinger;
    private Button buttonShowResults;
    private Button buttonShowChartAllResults;
    private Button buttonPostFacebook;
    private RelativeLayout resultsScrollView;
    private ProgressBar progressBar;

    private SharedPreferences prefs;
    private ArrayAdapter<String> adapter;
    private Context context;

    private String hand = Constants.RIGHT_HAND;
    private String finger = Constants.THUMB_FINGER;
    private String resultsMaxClicks;
    private String resultsSummaryClicks;
    private String resultsAmountOfTests;
    private String resultsMinClicks;
    private LinearSeries seriesSum;
    private int maxI = 0;

    private static final List<String> PERMISSIONS = Arrays
            .asList("publish_actions");
    private Editor editor;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        boolean isChecked = prefs.getBoolean(Constants.USER_FRAGMENT + "_CHECKED", false);
        if(!isChecked) {
            editor.putString(Constants.FRAGMENT_NAME, Constants.USER_FRAGMENT);
            editor.commit();

            Intent i = new Intent(context,
                    GuideModalActivity.class);
            startActivity(i);
        }
        // Monitor launch times and interval from installation
        RateThisApp.onStart(context);
        // Show a dialog if criteria is satisfied
        RateThisApp.showRateDialogIfNeeded(context);

        summaryResults = (TextView) rootView.findViewById(R.id.textSummaryClicks);
        nameView = (TextView) rootView.findViewById(R.id.textViewName);
        genderView = (TextView) rootView.findViewById(R.id.textViewGender);
        ageView = (TextView) rootView.findViewById(R.id.textViewAge);
        imgIcon = (ImageView) rootView.findViewById(R.id.imageProfileHome);
        dropMenuHand = (Spinner) rootView.findViewById(R.id.dropMenuHand);
        dropMenuFinger = (Spinner) rootView.findViewById(R.id.dropMenuFinger);
        buttonShowResults = (Button) rootView.findViewById(R.id.buttonShowResults);
        buttonPostFacebook = (Button) rootView.findViewById(R.id.buttonPostResultsFacebook);
        buttonShowChartAllResults = (Button) rootView.findViewById(R.id.buttonShowAllResults);
        textSummaryClicks = (TextView) rootView.findViewById(R.id.textSummaryClicksByHandAndFinger);
        textMaxClicks = (TextView) rootView.findViewById(R.id.textMaxClicksByHandAndFinger);
        textMinClicks = (TextView) rootView.findViewById(R.id.textMinClicksByHandAndFinger);
        textTitleResults = (TextView) rootView.findViewById(R.id.textTitleForStatisticsInScrollView);
        resultsScrollView = (RelativeLayout) rootView.findViewById(R.id.scrollViewHome);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressDialog);
        textChooseColorHighlevel = (TextView) rootView.findViewById(R.id.textChooseColorHighlevel);
        textChooseColorHighscore= (TextView) rootView.findViewById(R.id.textChooseColorHighscore);
        textRemColorHighlevel = (TextView) rootView.findViewById(R.id.textRemColorHighlevel);
        textRemColorHighscore= (TextView) rootView.findViewById(R.id.textRemColorHighscore);

        String[] fingers = new String[]{"Thumb", "Index", "Middle", "Ring", "Pinky"};
        setSpinnerAdapter(fingers, dropMenuFinger);

        String[] hands = new String[]{"Right", "Left"};
        setSpinnerAdapter(hands, dropMenuHand);

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String username = prefs.getString("USER_NAME", "Guest");
        String facebookId = prefs.getString("FACEBOOK_ID", "");
        String googlePlusImage = prefs.getString("GOOGLE_PLUS_IMAGE", "");
        String gender = prefs.getString("GENDER", "");
        String birthday = prefs.getString("BIRTHDAY", "");
        String age = "";

        if (!birthday.equals("")) {
            long birthdayDate = Date.parse(birthday);
            long todayDate = System.currentTimeMillis();
            long ageDate = (todayDate - birthdayDate) / (1000 * 60 * 60 * 24 * 365);
            age = String.valueOf(ageDate);

        }

        if(gender.equals("0")){
            gender="";
        }

        nameView.setText("Name: " + username);
        genderView.setText("Gender: " + gender);
        ageView.setText("Age: " + age);

        String imgUri = "";
        if (username.equals("Guest")) {
            imgIcon.setImageResource(R.drawable.ic_guest);
        } else {
            if(facebookId.equals("")) {
                imgUri = googlePlusImage;
                Picasso.with(context).load(imgUri).resize(150,150).into(imgIcon);
            }
            else {
                imgUri = "https://graph.facebook.com/" + facebookId
                        + "/picture?type=large";
                Picasso.with(context).load(imgUri).into(imgIcon);
            }
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

                editor.putString("FILTER",hand + "-" + finger);
                editor.commit();

                resultsScrollView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                resultsMaxClicks = getMaxValueOfClicks().toString();
                resultsMinClicks = getMinValueOfClicks().toString();
                resultsSummaryClicks = getSumValueOfClicks().toString();
                resultsAmountOfTests = getAmountOfTests().toString();
                progressBar.setVisibility(View.INVISIBLE);
                resultsScrollView.setVisibility(View.VISIBLE);
                textTitleResults.setText(String.format("Results are showed for %s hand and %s finger:", dropMenuHand.getSelectedItem(), dropMenuFinger.getSelectedItem()));
                textSummaryClicks.setText(String.format("Summary clicks after %s tests: %s", resultsAmountOfTests, resultsSummaryClicks));
                textMaxClicks.setText("Max clicks per 10 seconds: " + resultsMaxClicks);
                textMinClicks.setText("Min clicks per 10 seconds: " + resultsMinClicks);
                //TODO android graphs. Vertical - clicks, horizontal - seconds
                //http://stackoverflow.com/questions/9741300/charts-for-android
                //http://javapapers.com/android/android-chart-using-androidplot/

                // Find the chart view
                ChartView chartViewMaxResult = (ChartView) rootView.findViewById(R.id.chartViewMaxResults);

                //chartViewSummaryResults.setVisibility(View.VISIBLE);
                chartViewMaxResult.setVisibility(View.VISIBLE);

                // Create the data points
                seriesSum = new LinearSeries();

                int seconds = setSeriesForMaxResults();

                //int amountOfDifferentResults = getAmountsOfDifferentResults();

                ChartBuilder.buildChart(seriesSum, chartViewMaxResult, 2, seconds - 2, new ValueLabelAdapter(context, ValueLabelAdapter.LabelOrientation.VERTICAL), new ValueLabelAdapter(context, ValueLabelAdapter.LabelOrientation.HORIZONTAL));
                int highlevelRemColor = prefs.getInt(Constants.REM_COLOR_HIGHLEVEL, 0);
                int highscoreRemColor = prefs.getInt(Constants.REM_COLOR_HIGHSCORE, 0);
                int highlevelChooseColor = prefs.getInt(Constants.COLOR_HIGHLEVEL, 0);
                int highscoreChooseColor = prefs.getInt(Constants.COLOR_HIGHSCORE, 0);
                textChooseColorHighlevel.setText("High level: " + String.valueOf(highlevelChooseColor));
                textChooseColorHighscore.setText("High score: " + String.valueOf(highscoreChooseColor));
                textRemColorHighlevel.setText("High level: " + String.valueOf(highlevelRemColor));
                textRemColorHighscore.setText("High score: " + String.valueOf(highscoreRemColor));
            }
        });

        buttonShowChartAllResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,
                        ChartFragment.class);
                startActivity(i);
            }
        });

        buttonPostFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postStatusMessage();
            }
        });

        return rootView;
    }

    private int setSeriesForMaxResults() {
        String filter = hand + "-" + finger;
        Map<String, ?> allEntries = prefs.getAll();
        HashSet<String> set;
        LinkedList<String> list = new LinkedList<String>();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        int max = getMaxValueOfClicks();
        int k = 0;
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith(filter) && entry.getValue().toString().split(",").length == max) {
                for (String s : entry.getValue().toString().split(",")) {
                    list.add(s.replace("]", "").replace("[", "").trim());
                }
                break;
            }
        }
        Collections.sort(list);
        set = new HashSet<String>(list);
        LinkedList<String> newSet = new LinkedList<String>(set);
        Collections.sort(newSet);
        for (int n = 0; n < newSet.size(); n++) {
            int i = 0;
            String s = newSet.get(n);
            for (int m = 0; m < list.size(); m++) {
                if (list.get(m).toString().equals(s)) {
                    i++;
                }
                if (maxI < i) {
                    maxI = i;
                }
            }
            map.put(s, i);
        }
        int j = 0;

        SortedSet<String> keys = new TreeSet<String>(map.keySet());
        for (String key : keys) {
            j++;
            seriesSum.addPoint(new LinearSeries.LinearPoint(j, map.get(key)));
            k++;
        }

        if (k == 0) {
            seriesSum.addPoint(new LinearSeries.LinearPoint(0, 0));
        }
    return map.size();
    }

    private void setSpinnerAdapter(String[] data, Spinner spinner) {
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private Integer getMaxValueOfClicks() {
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
            Collections.sort(amount, Collections.reverseOrder());
            value = amount.getFirst();
        }
        return value;
    }

    private Integer getMinValueOfClicks() {
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

    private Integer getSumValueOfClicks() {
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

    private Integer getAmountOfTests() {
        Integer value = 0;
        String filter = hand + "-" + finger;
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith(filter)) {
                value++;
            }
        }
        return value;
    }

    private Integer getAmountsOfDifferentResults() {
        String filter = hand + "-" + finger;
        LinkedList<Integer> amount = new LinkedList<Integer>();
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith(filter)) {
                amount.add(entry.getValue().toString().split(",").length);
            }
        }

        Set<Integer> newAmount = new HashSet<Integer>(amount);

        return newAmount.size();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*
    Facebook post area
     */

    public boolean checkPermissions() {
        Session s = Session.getActiveSession();
        if (s != null) {
            return s.getPermissions().contains("publish_actions");
        } else
            return false;
    }

    public void requestPermissions() {
        Session s = Session.getActiveSession();
        if (s != null) {
            try {
                s.requestNewPublishPermissions(new Session.NewPermissionsRequest(getActivity(), PERMISSIONS));
            }
            catch (UnsupportedOperationException ex){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Login with Facebook");
                builder.setMessage("You need to login with Facebook account. Do You want to do it right now?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(Constants.IS_LOGGED_IN,false);
                        editor.commit();
                        dialog.dismiss();
                        Intent i = new Intent(getActivity(), StartActivity.class);
                        startActivity(i);
                    }

                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    public void postStatusMessage() {
        if (checkPermissions()) {
            Request request = Request.newStatusUpdateRequest(
                    Session.getActiveSession(), "I have very good reaction! Try Yourself with application https://play.google.com/store/apps/details?id="  + context.getPackageName(),
                    new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            if (response.getError() == null)
                                Toast.makeText(context,
                                        "Status updated successfully",
                                        Toast.LENGTH_LONG).show();
                        }
                    });
            request.executeAsync();
        } else {
            requestPermissions();
        }
    }
}
