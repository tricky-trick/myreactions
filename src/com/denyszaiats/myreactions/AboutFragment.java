package com.denyszaiats.myreactions;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class AboutFragment extends Fragment {

    private ImageView facebook;
    //private ImageView twitter;
    private ImageView gmail;
    private ImageView vkontakte;
    private RelativeLayout areaAboutSocial;
	
	public AboutFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        facebook = (ImageView) rootView.findViewById(R.id.imageAboutFacebook);
        //twitter = (ImageView) rootView.findViewById(R.id.imageAboutTwitter);
        gmail = (ImageView) rootView.findViewById(R.id.imageAboutGoogle);
        vkontakte = (ImageView) rootView.findViewById(R.id.imageAboutVk);
        areaAboutSocial = (RelativeLayout) rootView.findViewById(R.id.aboutSocialImgArea);

        AlphaAnimation animation= new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(1000);
        areaAboutSocial.startAnimation(animation);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("https://www.facebook.com/myreact");
            }
        });

        /*twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + "myreaction2014@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My feedback about MyReaction");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send email using..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "No email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        vkontakte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("https://vk.com/club83382802");
            }
        });

        return rootView;
    }

    private void openUrl(String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
