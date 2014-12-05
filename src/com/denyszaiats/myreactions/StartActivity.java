package com.denyszaiats.myreactions;

import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.content.IntentSender;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.Toast;
import com.denyszaiats.myreactions.R;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class StartActivity extends FragmentActivity implements OnClickListener,
		GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


	private static final int RC_SIGN_IN = 0;

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;

	/**
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;

	private boolean mSignInClicked;

	private ConnectionResult mConnectionResult;

	private SignInButton btnSignIn;
	private Button btnSignOut;

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

		btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
		btnSignOut = (Button) findViewById(R.id.btn_sign_out);

		// Button click listeners
		btnSignIn.setOnClickListener(this);
		btnSignOut.setOnClickListener(this);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build())
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();

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

	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	/**
	 * Button on click listener
	 * */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_sign_in:
				// Signin button clicked
				signInWithGplus();
				break;
			case R.id.btn_sign_out:
				// Signout button clicked
				signOutFromGplus();
				break;
		}
	}

	/**
	 * Sign-in into google
	 * */
	private void signInWithGplus() {
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	/**
	 * Method to resolve any signin errors
	 * */
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (IntentSender.SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	/**
	 * Sign-out from google
	 * */
	private void signOutFromGplus() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
			updateUI(false);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}

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

		if (requestCode == RC_SIGN_IN) {
			if (resultCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
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

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		updateUI(true);

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
		updateUI(false);
	}

	/**
	 * Updating the UI, showing/hiding buttons and profile layout
	 * */
	private void updateUI(boolean isSignedIn) {
		if (isSignedIn) {
			spinner.setVisibility(View.VISIBLE);
			btnSignIn.setVisibility(View.GONE);
			btnSignOut.setVisibility(View.VISIBLE);

			if (mGoogleApiClient.isConnected()) {
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);

				Editor editor = prefs.edit();
				editor.putString("USER_NAME", currentPerson.getName().getGivenName());
				editor.putString("GOOGLE_PLUS_IMAGE", currentPerson.getImage().getUrl());
				editor.putString("BIRTHDAY", currentPerson.getBirthday());
				editor.putString("GENDER", String.valueOf(currentPerson.getGender()));
				editor.commit();

				Intent i = new Intent(StartActivity.this,
						MainActivity.class);
				spinner.setVisibility(View.INVISIBLE);
				startActivity(i);

			}


		} else {
			btnSignIn.setVisibility(View.VISIBLE);
			btnSignOut.setVisibility(View.GONE);
		}
	}

}