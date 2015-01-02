package com.denyszaiats.myreactions;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class GuideModalActivity extends Activity{

    private CheckBox dontShowAgain;
    private TextView textGuide;
    private SharedPreferences prefs;
    private Button closeGuide;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modal_guide);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        dontShowAgain = (CheckBox) findViewById(R.id.dontShowAgain);
        textGuide = (TextView) findViewById(R.id.textGuide);
        closeGuide = (Button) findViewById(R.id.buttonGuideOk);
        String guideText = "";

        final String parentFragmentName = prefs.getString(Constants.FRAGMENT_NAME, "");

        if(parentFragmentName.equals(Constants.USER_FRAGMENT)){
            guideText = "Here You can monitor all results and activities of this application.\n" +
                    "You are able to choose a Finger and a Hand to see all results of Your clicking.\n" +
                    "It's possible to monitor the best and the worst results and results of whole clicking.\n" +
                    "Also You can see high score results for all games of this application.  All results can be published on Facebook.";
        }
        else if(parentFragmentName.equals(Constants.FAST_CLICKER_FRAGMENT)){
            guideText = "You have to do so many clicks as You can do per 10 seconds.\n" +
                    "You have possibility to choose a Hand and a Finger for every game to try each of them.\n" +
                    "After the end of 10-second game You can see the chart of Your clicking activity during the last game and see how many clicks per each second (in range from 0 to 10 seconds) You did. Good luck!";
        }
        else if(parentFragmentName.equals(Constants.CHOOSE_COLOR_FRAGMENT)){
            guideText =  "You have to recognize a rectangle with appropriate color and click on it as soon as possible while it's appearing. You have 30 seconds per level.\n" +
                    "The number of rectangles and time of their appearance increase with each level.\n" +
                    "You need to collect not less than 100 points per each level.\n" +
                    "Good luck!";
        }
        else if(parentFragmentName.equals(Constants.REMEMBER_COLOR_FRAGMENT)){
            guideText = "You have to memorise colors of rectangles and their position on the screen. You have 10 seconds for this. Then You need to click on the place where the rectangle with appropriate color was located." +
                    "You get 10 points and +1 life by clicking on the correct place.\n" +
                    "When you are ready and time has not finished (10 seconds) You can click button 'Ready'. In case of correct click You win additional points from seconds left and +1 life.\n" +
                    "You decrease Your lives  (-1) by clicking on incorrect places.\n" +
                    "You can choose the hint by clicking on the lamp icon and in this case You lose 2 lives automatically.\n" +
                    "The number of rectangles increases with each level.\n" +
                    "Good luck!";

        }

        closeGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dontShowAgain.isChecked()) {
                    editor.putBoolean(parentFragmentName + "_CHECKED", true);
                    editor.commit();
                }
               onBackPressed();
            }
        });

        textGuide.setText(guideText);
    }
}
