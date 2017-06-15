package com.sherdle.universal;

import com.loopj.android.http.*;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.sherdle.universal.R;
import com.sherdle.universal.facebook.FacebookFragment;
import com.sherdle.universal.fav.ui.FavFragment;
import com.sherdle.universal.instagram.InstagramFragment;
import com.sherdle.universal.maps.MapsFragment;
import com.sherdle.universal.media.ui.MediaFragment;
import com.sherdle.universal.rss.ui.RssFragment;
import com.sherdle.universal.tumblr.ui.TumblrFragment;
import com.sherdle.universal.twi.ui.TweetsFragment;
import com.sherdle.universal.web.WebviewFragment;
import com.sherdle.universal.wordpress.ui.WordpressFragment;
import com.sherdle.universal.yt.ui.VideosFragment;

import cz.msebera.android.httpclient.Header;

public class Config {

    public static List<NavItem> configuration() {

        List<NavItem> i = new ArrayList<NavItem>();

        //DONT MODIFY ABOVE THIS LINE
        i.add(new NavItem("Youtube", NavItem.SECTION));
        i.add(new NavItem("Videolar", R.drawable.icon_play, NavItem.ITEM, VideosFragment.class, "UUIgT1jUQFCmnoYtAiTu_0mw,UCIgT1jUQFCmnoYtAiTu_0mw"));
        i.add(new NavItem("Olmamış Mı? - Edis", R.drawable.icon_star, NavItem.ITEM, VideosFragment.class, "PLud3UdHzqC06hJSN_FNFHdHpkh62WyTP9"));

        AsyncHttpClient client = new AsyncHttpClient();
        RequestHandle items = client.get("https://www.googleapis.com/youtube/v3/playlists?part=snippet&channelId=UC8irbw_aqVhT_Z4zchvrL5Q&key=AIzaSyCUciyCmIcrHWDBaOq-oKUJTEoVCrJeL1Y", new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                        String result = response.toString();


                        JSONObject obj = null;
                        JSONArray arr = null;
                        try {
                            obj = new JSONObject(result);
                            arr = obj.getJSONArray("items");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int i = 0; i < arr.length(); i++) {
                            String pltitle = null;
                            String plid = null;
                            try {
                                pltitle = arr.getJSONObject(i).getJSONObject("snippet").getString("title");
                                plid = arr.getJSONObject(i).getString("id");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            i.add(new NavItem(pltitle, R.drawable.icon_star, NavItem.ITEM, VideosFragment.class, plid + ",UCIgT1jUQFCmnoYtAiTu_0mw"));


                        }
                    }
                        @Override
                        public void onFailure ( int statusCode, Header[] headers,
                        byte[] errorResponse, Throwable e){
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        }

                        @Override
                        public void onRetry ( int retryNo){
                            // called when request is retried
                        }
                    }

        );


            i.add(new NavItem("Sosyal Medya",NavItem.SECTION));
            i.add(new NavItem("Facebook",R.drawable.facebook, NavItem.ITEM, FacebookFragment.class, "165690820135520"));
            i.add(new NavItem("Twitter",R.drawable.twitter, NavItem.ITEM, TweetsFragment.class, "PDNDMusic"));
        
        //It's Suggested to not change the content below this line
        
         /* i.add(new NavItem("Device", NavItem.SECTION));
        i.add(new NavItem("Favorites", R.drawable.ic_action_favorite, NavItem.EXTRA, FavFragment.class, null));
        i.add(new NavItem("Settings", R.drawable.ic_action_settings, NavItem.EXTRA, SettingsFragment.class, null));
        */
        //DONT MODIFY BELOW THIS LINE
        
        return i;
			
	}
	
}