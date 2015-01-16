package com.denyszaiats.myreactions;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class LangActivity extends Activity {

    private ImageView flagEn;
    private ImageView flagUa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_modal_lang);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = prefs.edit();

        flagEn = (ImageView) findViewById(R.id.imageLangEn);
        flagUa = (ImageView) findViewById(R.id.imageLangUa);

        String prefLang = prefs.getString(Constants.LANG_PREFIX, "");
        if(!prefLang.equals("")) {
            Intent i = new Intent(LangActivity.this,
                    StartActivity.class);
            startActivity(i);
        }

        flagEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(Constants.LANG_PREFIX, "_en");
                editor.commit();
                Intent i = new Intent(LangActivity.this,
                        StartActivity.class);
                startActivity(i);
            }
        });

        flagUa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(Constants.LANG_PREFIX, "_ua");
                editor.commit();
                Intent i = new Intent(LangActivity.this,
                        StartActivity.class);
                startActivity(i);
            }
        });
    }
}