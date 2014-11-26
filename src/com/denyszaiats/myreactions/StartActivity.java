package com.denyszaiats.myreactions;

import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.denyszaiats.myreactions.R;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;

public class StartActivity extends FragmentActivity {

	// nice example
	// http://javatechig.com/android/using-facebook-sdk-in-android-example
	// http://www.androidhive.info/2014/02/android-login-with-google-plus-account-1/
	// http://www.androidhive.info/2013/11/android-sliding-menu-using-navigation-drawer/
	private SharedPreferences prefs;

	private ProgressBar spinner;

	private LoginButton loginBtn;

	private UiLifecycleHelper uiHelper;

	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");

	// private static String message = "Sample status posted from android app";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		uiHelper = new UiLifecycleHelper(this, statusCallback);
		uiHelper.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		setContentView(R.layout.activity_start);

		ImageView logo = (ImageView) findViewById(R.id.imageViewLogo);
		spinner = (ProgressBar) findViewById(R.id.spinner);

		logo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				RotateAnimation anim = new RotateAnimation(0.0f, 360.0f,
						Animation.RELATIVE_TO_SELF, .5f,
						Animation.RELATIVE_TO_SELF, .5f);
				anim.setInterpolator(new LinearInterpolator());
				anim.setRepeatCount(Animation.ABSOLUTE);
				anim.setDuration(1000);
				view.setAnimation(anim);
				view.startAnimation(anim);
			}
		});

		loginBtn = (LoginButton) findViewById(R.id.fb_login_button);

		new LoginAsyncTask().execute();
	}

	private class LoginAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... args) {
			loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback() {
				@Override
				public void onUserInfoFetched(GraphUser user) {
					if (user != null) {
						Editor editor = prefs.edit();
						editor.putString("USER_NAME", user.getFirstName());
						editor.putString("FACEBOOK_ID", user.getId());
						editor.putString("BIRTHDAY", user.getBirthday());
						editor.putString("GENDER", user.asMap().get("gender").toString());
						editor.commit();
						Intent i = new Intent(StartActivity.this,
								MainActivity.class);
						startActivity(i);
					} else {
						Editor editor = prefs.edit();
						editor.putString("USER_NAME", "Guest");
						editor.putString("FACEBOOK_ID", "");
						editor.putString("BIRTHDAY", "");
						editor.putString("GENDER", "");
						editor.commit();
					}
				}
			});
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					spinner.setVisibility(View.INVISIBLE);
				}
			});
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					spinner.setVisibility(View.VISIBLE);
				}
			});
		}
	}

	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (state.isOpened()) {
				Log.d("FacebookSampleActivity", "Facebook session opened");
			} else if (state.isClosed()) {
				Log.d("FacebookSampleActivity", "Facebook session closed");
			}
		}
	};

	// public void postImage() {
	// if (checkPermissions()) {
	// Bitmap img = BitmapFactory.decodeResource(getResources(),
	// R.drawable.ic_launcher);
	// Request uploadRequest = Request.newUploadPhotoRequest(
	// Session.getActiveSession(), img, new Request.Callback() {
	// @Override
	// public void onCompleted(Response response) {
	// Toast.makeText(StartActivity.this,
	// "Photo uploaded successfully",
	// Toast.LENGTH_LONG).show();
	// }
	// });
	// uploadRequest.executeAsync();
	// } else {
	// requestPermissions();
	// }
	// }
	//
	// public void postStatusMessage() {
	// if (checkPermissions()) {
	// Request request = Request.newStatusUpdateRequest(
	// Session.getActiveSession(), message,
	// new Request.Callback() {
	// @Override
	// public void onCompleted(Response response) {
	// if (response.getError() == null)
	// Toast.makeText(StartActivity.this,
	// "Status updated successfully",
	// Toast.LENGTH_LONG).show();
	// }
	// });
	// request.executeAsync();
	// } else {
	// requestPermissions();
	// }
	// }

	public boolean checkPermissions() {
		Session s = Session.getActiveSession();
		if (s != null) {
			return s.getPermissions().contains("publish_actions");
		} else
			return false;
	}

	public void requestPermissions() {
		Session s = Session.getActiveSession();
		if (s != null)
			s.requestNewPublishPermissions(new Session.NewPermissionsRequest(
					this, PERMISSIONS));
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		uiHelper.onSaveInstanceState(savedState);
	}

	public void goToApp(View v) {
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}

}