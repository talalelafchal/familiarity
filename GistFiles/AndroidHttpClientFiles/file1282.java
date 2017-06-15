package com.codepath.apps.mysimpletwitter;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletwitter.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity{
    TwitterClient client;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        client = TwitterApplication.getRestClient();
        String screenName = getIntent().getStringExtra("screen_name");
        String classname = getIntent().getStringExtra("class");

        if(classname.equals("userProfile")){
            Bundle bundle = getIntent().getExtras();
            user = bundle.getParcelable("user");
        }
        else {
            client.getAccountProfile(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    user = User.fromJSON(response);
                    getSupportActionBar().setTitle("@" + user.getScreenName());
                    populateProfileHeader(user);

                }
            });
        }

        //get the screen name from the activity we launched (HometimeLineActivity)

        if(savedInstanceState == null) {
            //Create UserTimelineFragment
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);
            //Display the userTimelineFragment
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }

    }

    private void populateProfileHeader(User user) {
        ImageView ivProfile = (ImageView) findViewById(R.id.profile_img);
        TextView txProfileName = (TextView) findViewById(R.id.profile_name);
        TextView txProfileInfo = (TextView) findViewById(R.id.profile_info);
        TextView txFollowers = (TextView) findViewById(R.id.profile_followers);
        TextView txFollowings = (TextView) findViewById(R.id.profile_followings);

        Picasso.with(this).load(user.getProfileImgURL()).into(ivProfile);
        txProfileName.setText(user.getName());
        txProfileInfo.setText(user.getProfileDescription());
        txFollowers.setText(String.valueOf(user.getFollowerCount() + " Followers"));
        txFollowings.setText(String.valueOf(user.getFollowingCount() + " Followings"));

    }
}
