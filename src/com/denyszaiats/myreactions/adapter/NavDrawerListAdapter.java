package com.denyszaiats.myreactions.adapter;

import java.util.ArrayList;

import com.denyszaiats.myreactions.R;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavDrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<com.denyszaiats.myreactions.model.NavDrawerItem> navDrawerItems;
	private SharedPreferences prefs;
	private ImageView imgIcon;

	public NavDrawerListAdapter(
			Context context,
			ArrayList<com.denyszaiats.myreactions.model.NavDrawerItem> navDrawerItems) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.drawer_list_item, null);
		}

		imgIcon = (ImageView) convertView.findViewById(R.id.icon);
		TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
		TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String username = prefs.getString("USER_NAME", "Guest");
		String facebookId = prefs.getString("FACEBOOK_ID", "");
		String imgUri = "";
		if (position == 0) {
			if (username.equals("Guest")) {
				imgIcon.setImageResource(R.drawable.ic_guest);
			} else {
				imgUri = "https://graph.facebook.com/" + facebookId
						+ "/picture?type=square";
				Picasso.with(context).load(imgUri).resize(96, 96).into(imgIcon);
			}
		} else {
			imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
		}
		txtTitle.setText(navDrawerItems.get(position).getTitle());

		// displaying count
		// check whether it set visible or not
		if (navDrawerItems.get(position).getCounterVisibility()) {
			txtCount.setText(navDrawerItems.get(position).getCount());
		} else {
			// hide the counter view
			txtCount.setVisibility(View.GONE);
		}

		return convertView;
	}

}
