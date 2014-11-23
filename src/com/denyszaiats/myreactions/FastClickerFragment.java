package com.denyszaiats.myreactions;

import java.util.Date;
import java.util.LinkedList;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FastClickerFragment extends Fragment {

	/*
	 * Add functionality for each fingers and both hands
	 */
	
	private TextView chronometer;
	private TextView resultsView;
	private Button startTimer;
	private Button tryAgain;
	private ImageView imageThree;
	private ImageView imageTwo;
	private ImageView imageOne;
	private ImageView tapButton;
	private Context context;
	private LinkedList<String> results;
	private SharedPreferences prefs;
	private int summaryClicks;
	private int maxTempClicks;
	private static String LEFT_HAND = "LH";
	private static String RIGHT_HAND = "RH";
	private static String THUMB_FINGER = "T";
	private static String INDEX_FINGER = "I";
	private static String MIDDLE_FINGER = "M";
	private static String RING_FINGER = "R";
	private static String PINKY_FINGER = "P";

	public FastClickerFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_fast_clicker,
				container, false);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		context = container.getContext();
		chronometer = (TextView) rootView
				.findViewById(R.id.chronometerFasterClicker);
		resultsView = (TextView) rootView
				.findViewById(R.id.resultsFasterClicker);
		startTimer = (Button) rootView.findViewById(R.id.startButton);
		tryAgain = (Button) rootView.findViewById(R.id.tryAgainButton);
		imageThree = (ImageView) rootView.findViewById(R.id.imageThree);
		imageTwo = (ImageView) rootView.findViewById(R.id.imageTwo);
		imageOne = (ImageView) rootView.findViewById(R.id.imageOne);
		tapButton = (ImageView) rootView.findViewById(R.id.imageTapButton);
		results = new LinkedList<String>();
		startTimer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setVisibility(View.INVISIBLE);
				//doAnimation(imageThree);
				//doAnimation(imageTwo);
				//doAnimation(imageOne);
				doAnimation(tapButton);
				//tapButton.setVisibility(View.VISIBLE);
				chronometer.setVisibility(View.VISIBLE);
				CountDownTimer cT = new CountDownTimer(11000, 1000) {

					public void onTick(long millisUntilFinished) {
						int va = (int) ((millisUntilFinished % 60000) / 1000);
						chronometer.setText("00:" + String.format("%02d", va));
					}

					public void onFinish() {
						ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f,
				                0.0f, 1.0f);
						animation.setDuration(1000); 
						chronometer.setText("Finish");
						chronometer.startAnimation(animation);
						tapButton.setVisibility(View.INVISIBLE);
						resultsView.setText(String.valueOf(results.size()));
						resultsView.setVisibility(View.VISIBLE);
						tryAgain.setVisibility(View.VISIBLE);
					}
				};
				cT.start();

			}
		});
		
		tapButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Date date = new Date();
				long date = System.currentTimeMillis() / 1000L;
				results.add(String.valueOf(date));
			}
		});
		
		tryAgain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startTimer.setVisibility(View.VISIBLE);
				resultsView.setVisibility(View.INVISIBLE);
				chronometer.setVisibility(View.INVISIBLE);
				results.clear();
				v.setVisibility(View.INVISIBLE);
			}
		});

		return rootView;
	}

	private void doAnimation(ImageView image) {
		image.setVisibility(View.VISIBLE);
		ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f,
                0.0f, 1.0f);
        animation.setDuration(1000); 
        image.setAnimation(animation);
		//image.setVisibility(View.INVISIBLE);
	}
	
	private void sleep(float sec){
		try {
			Thread.sleep((long) (sec * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
