package com.denyszaiats.myreactions;

import java.util.LinkedList;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
	private Button handButton;
	private Button fingerButton;
	private ImageView tapButton;
	private ImageView leftHand;
	private ImageView rightHand;
	private ImageView fingerThumb;
	private ImageView fingerIndex;
	private ImageView fingerMiddle;
	private ImageView fingerRing;
	private ImageView fingerPinky;
	private Context context;
	private LinkedList<String> results;
	private SharedPreferences prefs;
	private Editor editor;
	private int summaryClicks;
	private int maxTempClicks;
	private String hand = Constants.RIGHT_HAND;
	private String finger = Constants.INDEX_FINGER;

	public FastClickerFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_fast_clicker,
				container, false);
		context = container.getContext();
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		summaryClicks = Integer.valueOf(prefs.getString(Constants.SUMMARY_CLICKS, "0"));
		chronometer = (TextView) rootView
				.findViewById(R.id.chronometerFasterClicker);
		resultsView = (TextView) rootView
				.findViewById(R.id.resultsFasterClicker);

		// Base
		startTimer = (Button) rootView.findViewById(R.id.startButton);
		tryAgain = (Button) rootView.findViewById(R.id.tryAgainButton);
		handButton = (Button) rootView.findViewById(R.id.handButton);
		fingerButton = (Button) rootView.findViewById(R.id.fingerButton);
		tapButton = (ImageView) rootView.findViewById(R.id.imageTapButton);
		// Hands
		leftHand = (ImageView) rootView.findViewById(R.id.imageHandLeft);
		rightHand = (ImageView) rootView.findViewById(R.id.imageRightHand);
		// Fingers
		fingerThumb = (ImageView) rootView.findViewById(R.id.imageThumbFinger);
		fingerIndex = (ImageView) rootView.findViewById(R.id.imageIndexFinger);
		fingerMiddle = (ImageView) rootView.findViewById(R.id.imageMiddleFinger);
		fingerRing = (ImageView) rootView.findViewById(R.id.imageRingFinger);
		fingerPinky = (ImageView) rootView.findViewById(R.id.imagePinkyFinger);
		
		results = new LinkedList<String>();
		startTimer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setVisibility(View.INVISIBLE);
				doAnimation(tapButton);
				chronometer.setVisibility(View.VISIBLE);
				CountDownTimer cT = new CountDownTimer(11000, 1000) {

					public void onTick(long millisUntilFinished) {
						int va = (int) ((millisUntilFinished % 60000) / 1000);
						chronometer.setText("00:" + String.format("%02d", va));
					}

					public void onFinish() {
						ScaleAnimation animation = new ScaleAnimation(0.0f,
								1.0f, 0.0f, 1.0f);
						animation.setDuration(1000);
						chronometer.setText("Finish");
						chronometer.startAnimation(animation);
						tapButton.setVisibility(View.INVISIBLE);
						resultsView.setText(String.valueOf(results.size()));
						resultsView.setVisibility(View.VISIBLE);
						tryAgain.setVisibility(View.VISIBLE);
						summaryClicks += results.size();
						editor = prefs.edit();
						editor.putString(Constants.SUMMARY_CLICKS, String.valueOf(summaryClicks));
						editor.putString(String.format("%s-%s-%s", hand, finger, String.valueOf(System.currentTimeMillis() / 1000L)), results.toString());
						editor.commit();
						
					}
				};
				cT.start();

			}
		});

		tapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Date date = new Date();
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

		handButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (startTimer.getVisibility() == View.VISIBLE) {
					leftHand.setVisibility(View.VISIBLE);
					rightHand.setVisibility(View.VISIBLE);
					doAnimation(leftHand);
					doAnimation(rightHand);
				}
			}
		});

		leftHand.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hand = Constants.LEFT_HAND;
				leftHand.setVisibility(View.INVISIBLE);
				rightHand.setVisibility(View.INVISIBLE);
				handButton.setText("Left hand");
			}
		});

		rightHand.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hand = Constants.RIGHT_HAND;
				leftHand.setVisibility(View.INVISIBLE);
				rightHand.setVisibility(View.INVISIBLE);
				handButton.setText("Right hand");
			}
		});

		fingerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (startTimer.getVisibility() == View.VISIBLE) {
					setFingersVisibility(View.VISIBLE);
					doAnimation(fingerThumb);
					doAnimation(fingerIndex);
					doAnimation(fingerMiddle);
					doAnimation(fingerRing);
					doAnimation(fingerPinky);
				}
			}
		});

		fingerThumb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finger = Constants.THUMB_FINGER;
				setFingersVisibility(View.INVISIBLE);
				fingerButton.setText("Thumb finger");
			}
		});

		fingerIndex.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finger = Constants.INDEX_FINGER;
				setFingersVisibility(View.INVISIBLE);
				fingerButton.setText("Index finger");
			}
		});

		fingerMiddle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finger = Constants.MIDDLE_FINGER;
				setFingersVisibility(View.INVISIBLE);
				fingerButton.setText("Middle finger");
			}
		});

		fingerRing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finger = Constants.RING_FINGER;
				setFingersVisibility(View.INVISIBLE);
				fingerButton.setText("Ring finger");
			}
		});

		fingerPinky.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finger = Constants.PINKY_FINGER;
				setFingersVisibility(View.INVISIBLE);
				fingerButton.setText("Pinky finger");
			}
		});

		return rootView;
	}

	private void doAnimation(ImageView image) {
		image.setVisibility(View.VISIBLE);
		ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f);
		animation.setDuration(1000);
		image.setAnimation(animation);
	}

	private void setFingersVisibility(int visibility) {
		fingerThumb.setVisibility(visibility);
		fingerIndex.setVisibility(visibility);
		fingerMiddle.setVisibility(visibility);
		fingerRing.setVisibility(visibility);
		fingerPinky.setVisibility(visibility);
	}

	private void sleep(float sec) {
		try {
			Thread.sleep((long) (sec * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
