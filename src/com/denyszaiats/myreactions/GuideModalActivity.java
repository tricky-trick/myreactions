package com.denyszaiats.myreactions;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GuideModalActivity extends Activity{

    private RelativeLayout mainLayout;
    private CheckBox dontShowAgain;
    private WebView textGuide;
    private TextView titleGuide;
    private SharedPreferences prefs;
    private Button closeGuide;
    private SharedPreferences.Editor editor;
    private String prefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modal_guide);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefix = prefs.getString(Constants.LANG_PREFIX, "_en");
        editor = prefs.edit();
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayoutGuide);
        dontShowAgain = (CheckBox) findViewById(R.id.dontShowAgain);{
            dontShowAgain.setText(Helper.setStringFromResources(this, "dont_show_again_checkbox" + prefix));
        }
        textGuide = (WebView) findViewById(R.id.textGuide);
        titleGuide = (TextView) findViewById(R.id.textGuideTitle);{
            titleGuide.setText(Helper.setStringFromResources(this, "title_guide" + prefix));
        }
        closeGuide = (Button) findViewById(R.id.buttonGuideOk);
        String guideText = "";

        String userFragmentText = Helper.setStringFromResources(this, "guide_home_page" + prefix);

        String fastClickerFragmentText = Helper.setStringFromResources(this, "guide_fast_clicker" + prefix);

        String chooseColorFragmentText = Helper.setStringFromResources(this, "guide_choose_color" + prefix);

        String remColorFragmentText = Helper.setStringFromResources(this, "guide_rem_color" + prefix);


        final String parentFragmentName = prefs.getString(Constants.FRAGMENT_NAME, "");

        if(parentFragmentName.equals(Constants.USER_FRAGMENT)){
            guideText = userFragmentText;
        }
        else if(parentFragmentName.equals(Constants.FAST_CLICKER_FRAGMENT)) {
            guideText = fastClickerFragmentText;
        }
        else if(parentFragmentName.equals(Constants.CHOOSE_COLOR_FRAGMENT)){
            guideText = chooseColorFragmentText;
        }
        else if(parentFragmentName.equals(Constants.REMEMBER_COLOR_FRAGMENT)){
            guideText = remColorFragmentText;
        }

        else if(parentFragmentName.equals(Constants.HELP_FRAGMENT)){
            guideText = "<h3>" + Helper.setStringFromResources(this, "home_title" + prefix) + "</h3>" + userFragmentText +
                    "<br><h3>" + Helper.setStringFromResources(this, "rem_color_title" + prefix) + "</h3>" + remColorFragmentText +
                        "<br><h3>" + Helper.setStringFromResources(this, "fast_clicker_title" + prefix) + "</h3>" + fastClickerFragmentText +
                        "<br><h3>" + Helper.setStringFromResources(this, "choose_color_title" + prefix) + "</h3>" + chooseColorFragmentText;
            mainLayout.removeView(dontShowAgain);
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
        String customHtml = "<html>" +
                                "<head>" +
                                    "<style>" +
                                        "body{" +
                                        "color: #000000;" +
                                        "text-align:justify;" +
                                        "font-family: Arial, Helvetica, sans-serif;" +
                                        "font-size:14dp;}" +
                                        "h3{" +
                                        "text-align:center;" +
                                        "font-size:16dp;}" +
                                    "</style>" +
                                "</head>" +
                                "<body>" + guideText + "</body>" +
                            "</html>";
        textGuide.loadData(customHtml, "text/html; charset=UTF-8", "UTF-8");
    }
}
