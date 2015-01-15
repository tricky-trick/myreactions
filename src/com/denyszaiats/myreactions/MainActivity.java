package com.denyszaiats.myreactions;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;
import com.denyszaiats.myreactions.adapter.NavDrawerListAdapter;
import com.denyszaiats.myreactions.model.NavDrawerItem;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.android.gms.common.api.GoogleApiClient;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity{
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	
	private SharedPreferences prefs;

	private ArrayList<com.denyszaiats.myreactions.model.NavDrawerItem> navDrawerItems;
	private com.denyszaiats.myreactions.adapter.NavDrawerListAdapter adapter;
	private GoogleApiClient mGoogleApiClient;
	private int MENU_ITEM_ITEM1 = 1;
	private int MENU_ITEM_ITEM2 = 1;
	private String prefix;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefix = prefs.getString(Constants.LANG_PREFIX, "_en");

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(getResources().getIdentifier("nav_drawer_items" + prefix, "array", getPackageName()));
		//navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items_en);
		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		String username = prefs.getString("USER_NAME", "Guest");

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Fast Clicker
		String sumClicks = prefs.getString(Constants.SUMMARY_CLICKS, "");
		if (sumClicks.equals(""))
			sumClicks = "0";

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1), false, sumClicks));
		// Choose color
		int highscore = prefs.getInt(Constants.COLOR_HIGHSCORE, 0);
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1), false, String.valueOf(highscore)));
		// Remember color
		int highscoreRemColor = prefs.getInt(Constants.REM_COLOR_HIGHSCORE, 0);
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), false, String.valueOf(highscoreRemColor)));

		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// About
		try {
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "v." + MainActivity.this.getPackageManager()
                    .getPackageInfo(MainActivity.this.getPackageName(), 0).versionName));
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}


		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(this,
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		new Handler().post(openDrawerRunnable());

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
	}

	private Runnable openDrawerRunnable() {
		return new Runnable() {

			@Override
			public void run() {
				mDrawerLayout.openDrawer(Gravity.LEFT);
			}
		};
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
		MenuItem item1 = menu.add(Menu.NONE,
				MENU_ITEM_ITEM1,
				Menu.NONE,
				Helper.setStringFromResources(this, "clean_results" + prefix));
		item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				TextView msg = new TextView(MainActivity.this);
				msg.setText(Helper.setStringFromResources(MainActivity.this, "dialog_msg_delete_rslt" + prefix));
				msg.setPadding(20, 10, 20, 10);
				msg.setGravity(Gravity.CENTER);
				msg.setTextSize(20);
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

				builder.setTitle(Helper.setStringFromResources(MainActivity.this, "dialog_title_delete_rslt" + prefix));
				builder.setView(msg);

				builder.setPositiveButton(Helper.setStringFromResources(MainActivity.this, "dialog_yes" + prefix), new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences.Editor editor = prefs.edit();
						editor.clear();
						editor.putBoolean(Constants.IS_LOGGED_IN,false);
						editor.commit();
						dialog.dismiss();
						Intent i = new Intent(MainActivity.this, StartActivity.class);
						startActivity(i);
					}

				});

				builder.setNegativeButton(Helper.setStringFromResources(MainActivity.this, "dialog_no" + prefix), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				AlertDialog alert = builder.create();
				alert.show();

				return true;
			}
		});

		MenuItem item2 = menu.add(Menu.NONE,
				MENU_ITEM_ITEM2,
				Menu.NONE,
				Helper.setStringFromResources(this, "logout_menu" + prefix));
		item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean(Constants.IS_LOGGED_IN,false);
				editor.commit();
				Intent i = new Intent(MainActivity.this, StartActivity.class);
				startActivity(i);
				return true;
			}
		});
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
 
    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_logout).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
			case 0:
				fragment = new HomeFragment();
				break;
			case 1:
				fragment = new FastClickerFragment();
				break;
			case 2:
				fragment = new ChooseColorFragment();
				break;
			case 3:
				fragment = new RememberColorFragment();
				break;
			case 4:
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(Constants.FRAGMENT_NAME, Constants.HELP_FRAGMENT);
				editor.commit();
				Intent i = new Intent(this,
						GuideModalActivity.class);
				startActivity(i);
				break;
			case 5:
				fragment = new AboutFragment();
				break;
			default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

//	@Override
//	public void onBackPressed() {
//		// do nothing.
//	}

}
