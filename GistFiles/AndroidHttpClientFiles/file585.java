package com.codepath.apps.mysimpletwitter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.codepath.apps.mysimpletwitter.ProfileActivity;
import com.codepath.apps.mysimpletwitter.TweetsArrayAdapter;
import com.codepath.apps.mysimpletwitter.TwitterApplication;
import com.codepath.apps.mysimpletwitter.TwitterClient;
import com.codepath.apps.mysimpletwitter.models.Tweet;
import com.codepath.apps.mysimpletwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by joanniehuang on 2017/3/10.
 */

public class HomeTimelineFragment extends TweetsListFragment{
    private TwitterClient client;
    private RecyclerView listView;
    private final int REQUEST_CODE = 200;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        listView = super.getListView();
        populateTimeline();

        //set the item onClickOnListener
        getAdapter().setOnItemClickListener(new TweetsArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Intent i = new Intent(getActivity(), ProfileActivity.class);
                User user = getAdapter().getItem(position).getUser();
                i.putExtra("screen_name", user.getScreenName());
                i.putExtra("class", "userProfile");
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", user);

                //i.putExtra("user", user);
                //i.putExtra("user", Parcels.wrap(user));

                /*i.putExtra("user_name", user.getName());
                i.putExtra("user_info", user.getProfileDescription());
                i.putExtra("followers", user.getFollowerCount());
                i.putExtra("profile_img", user.getProfileImgURL());
                i.putExtra("following", user.getFollowingCount());*/
                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            //SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray array){
                addAll(Tweet.fromJSONArray(array));
                getAdapter().notifyDataSetChanged();
            }

            //FAILED
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }

        });
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(long maxid) {

        client.getHomeTimeline(maxid, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray array) {
                addAll(Tweet.fromJSONArray(array));
                getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

            }
        });
    }
}
