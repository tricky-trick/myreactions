package com.denyszaiats.myreactions;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {
	
	private TextView summaryResults;
	private SharedPreferences prefs;
	private Context context;
	public HomeFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		context = container.getContext();
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        summaryResults = (TextView) rootView.findViewById(R.id.txtLabel);
        summaryResults.setText(prefs.getString(Constants.SUMMARY_CLICKS, ""));
        return rootView;
    }
}
