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
            guideText = "   This is main window where user could monitor all results and activity of this application.\n" +
                    "   User is able to choose Finger and Hand to look at results of clicking.\n" +
                    "   It's possible to monitor best, worth results and clicking results during long period of time.\n" +
                    "   It will help user to understand  - are results better or worth with each game.\n" +
                    "   Also it's possible to see high score results for another game-test of this application and publish results on Facebook";
        }
        else if(parentFragmentName.equals(Constants.FAST_CLICKER_FRAGMENT)){
            guideText = "   Fast clicker game. You need to so many clicks as You can per 10 seconds.\n" +
                    "   For this You need to choose Finger and Hand and click START.\n" +
                    "   You will see the chart of Your clicks during Your last session. You will be able to see how much clicks per each seconds You did.";
        }
        else if(parentFragmentName.equals(Constants.CHOOSE_COLOR_FRAGMENT)){
            guideText = "This is the activity with Choose Color";
        }
        else if(parentFragmentName.equals(Constants.REMEMBER_COLOR_FRAGMENT)){
            guideText = "This is the activity with Remembering Color";
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
