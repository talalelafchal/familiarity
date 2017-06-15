package bhh.youtube.channel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import bhh.youtube.channel.pojo.VideoPojo;
public class VideoList extends ActionBarActivity {
    RequestQueue mRequestQueue;
    ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;
    ImageLoader imageLoader;
    ListView lvvideos;
    String CHANNEL_ID;
    String YOUTUBE_URL = "";
    String NEXT_PAGE_TOKEN = "",PREV_PAGE_TOKEN="";
    ProgressDialog progress;
    int total = 0;
    ArrayList<VideoPojo> videolist = new ArrayList<VideoPojo>();
    Custom_Adapter adapter;
    boolean loadmore = false;
    TextView txtfooter;
    private AdView adView;
    private static final String AD_UNIT_ID = DataManager.ADMOB_BANNER;
    private InterstitialAd interstitial;
	ConnectionDetector connectionDetector;

    private static final String YOUTUBE_API_KEY = "youtube_api_key";
    private static final String ANDROID_YOUTUBE_API_KEY = "android_youtube_api_key";
    /**Variables For Remote Config*/
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
   // private TextView mWelcomeTextView;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(remoteConfigSettings);

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        fetchData();
        
        lvvideos = (ListView) findViewById(R.id.lvvideos);
        CHANNEL_ID = DataManager.selectedchannelid;

		connectionDetector = new ConnectionDetector(this);
        adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(AD_UNIT_ID);
        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);
        LinearLayout ll = (LinearLayout) findViewById(R.id.ad);
        ll.addView(adView);

        txtfooter = (TextView) findViewById(R.id.txtfooter);
        txtfooter.setVisibility(View.GONE);

        if(DataManager.type.equals("channel"))
        {
            Log.d("Call channel video","call channel");
            new loadvideos().execute();
        }
        else if(DataManager.type.equals("playlist"))
        {
            Log.d("Call playlist video","call playlist");
            new loadplaylistvideos().execute();
        }


 


        lvvideos.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (lvvideos.getLastVisiblePosition() >= lvvideos.getCount() - 1)
                    {
                        if(DataManager.type.equals("channel"))
                        {
                            if (loadmore)
                            {
                                new loadvideos().execute();
                                txtfooter.setText(" Loading more videos...");
                                txtfooter.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                txtfooter.setText("No More Videos");
                                txtfooter.setVisibility(View.GONE);
                            }
                        }
                        else if(DataManager.type.equals("playlist"))
                        {
                            if (loadmore)
                            {
                                Log.d("load","Loading More Videos---");
                                new loadplaylistvideos().execute();
                                txtfooter.setText(" Loading more videos...");
                                txtfooter.setVisibility(View.VISIBLE);
                            } else {
                                txtfooter.setText("No More Videos");
                                txtfooter.setVisibility(View.GONE);
                            }
                        }
                    }
                }


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


            }
        });

        lvvideos.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                DataManager.selectedvideoid = videolist.get(position).getVideoid();
                Intent i = new Intent(VideoList.this, YouTubePlayerActivity.class);
                i.putExtra("video_id",DataManager.selectedvideoid);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
        getSupportActionBar().setTitle(DataManager.channelname);

        // Begin loading your interstitial.
        AdRequest adRequest1 = new AdRequest.Builder().build();
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(DataManager.ADMOB_INTERSTIAL);

        interstitial.loadAd(adRequest1);
        AdListener adListener = new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();


            }
        };

        interstitial.setAdListener(adListener);
    }
    /**
     * Fetch welcome message from server.
     */
    private void fetchData() {
      //  mWelcomeTextView.setText(mFirebaseRemoteConfig.getString(YOUTUBE_API_KEY));

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
        // the server.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
        // fetched and cached config would be considered expired because it would have been fetched
        // more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
        // throttling is in progress. The default expiration duration is 43200 (12 hours).
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(VideoList.this, "Fetch Succeeded\n"+ mFirebaseRemoteConfig.getString(ANDROID_YOUTUBE_API_KEY),
                                   Toast.LENGTH_SHORT).show();
                            // Once the config is successfully fetched it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(VideoList.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        //displayWelcomeMessage();
                    }
                });
        // [END fetch_config_with_callback]
    }

    public void nointernet()
    {
        new AlertDialog.Builder(this)
                .setTitle("Connection Error")
                .setMessage("Try Again")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1)
                            {
                                if(DataManager.type.equals("channel"))
                                {
                                    new loadvideos().execute();
                                    arg0.dismiss();
                                }
                                else
                                {
                                    new loadplaylistvideos().execute();
                                    arg0.dismiss();
                                }
                                arg0.cancel();
                            }
                        }).create().show();
    }
    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }
    @Override
    public void onBackPressed() {

        Intent i = new Intent(VideoList.this, MainActivity.class);
        finish();
        startActivity(i);
        overridePendingTransition(0,0);

    }




    private class loadvideos extends AsyncTask<Void, Void, Void> {
        boolean isconnect = false;

        @Override
        protected void onPreExecute() {
            // Showing progress dialog before sending http request.
            if (!loadmore)
            {
                progress = GoogleProgress.Progressshow(VideoList.this);
                progress.show();
            }
        }

        protected Void doInBackground(Void... unused) {
            try
            {
                if(!connectionDetector.isConnectingToInternet())
                {
                    nointernet();
                }
                else
                {
                    HttpClient client = new DefaultHttpClient();
                    HttpConnectionParams.setConnectionTimeout(client.getParams(),
                            15000);
                    HttpConnectionParams.setSoTimeout(client.getParams(), 15000);

                    if (!loadmore)
                    {
                        YOUTUBE_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId="
                                + CHANNEL_ID
                                + "&type=video"
                                + "&maxResults="
                                + DataManager.maxResults + "&key=" + mFirebaseRemoteConfig.getString(ANDROID_YOUTUBE_API_KEY) + "&order=date";  // USE  Remote Config API Key
                    }
                    else
                    {
                        YOUTUBE_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&pageToken="
                                + NEXT_PAGE_TOKEN
                                + "&channelId="
                                + CHANNEL_ID
                                + "&type=video"
                                + "&maxResults="
                                + DataManager.maxResults
                                + "&key="
                                + mFirebaseRemoteConfig.getString(ANDROID_YOUTUBE_API_KEY) + "&order=date";   // USE  Remote Config API Key
                    }


                    HttpUriRequest request = new HttpGet(YOUTUBE_URL);

                    HttpResponse response = client.execute(request);

                    InputStream atomInputStream = response.getEntity().getContent();

                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            atomInputStream));

                    String line;
                    String str = "";
                    while ((line = in.readLine()) != null) {
                        str += line;
                    }

                    System.out.println("url---" + YOUTUBE_URL);

                    JSONObject json = new JSONObject(str);
                    JSONArray items = json.getJSONArray("items");

                    total = json.getJSONObject("pageInfo").getInt("totalResults");
                    if (total > 10)
                    {
                        if(json.has("nextPageToken"))
                        {
                            loadmore = true;
                            NEXT_PAGE_TOKEN = json.getString("nextPageToken");

                        }
                    }

                    for (int i = 0; i < items.length(); i++) {

                        VideoPojo video = new VideoPojo();
                        JSONObject youtubeObject = items.getJSONObject(i).getJSONObject("snippet");


                        if (items.getJSONObject(i).getJSONObject("id").getString("videoId") != null) {

                            video.setVideoid(items.getJSONObject(i).getJSONObject("id").getString("videoId"));
                            video.setTitle(youtubeObject.getString("title"));
                            video.setThumbnail(youtubeObject.getJSONObject("thumbnails").getJSONObject("high").getString("url"));

                            videolist.add(video);

                        }

                    }

                    isconnect = true;
                }
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                isconnect = false;
                System.out.println("1exception---" + e.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                isconnect = false;
                System.out.println("2exception---" + e.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                System.out.println("3exception---" + e.getMessage());
                e.printStackTrace();
            }

            return (null);
        }

        protected void onPostExecute(Void unused) {
            // Closing progress dialog.
            progress.dismiss();
            if (isconnect) {
                if (videolist.size() > 0) {
                    displayInterstitial();
                    adapter = new Custom_Adapter(getApplicationContext());
                    lvvideos.setAdapter(adapter);

                    if (loadmore)
                        lvvideos.setSelection(((videolist.size() - DataManager.maxResults)-1));
                    else
                        lvvideos.setSelection(0);


                    if (total > videolist.size()) {
                        loadmore = true;
                    }else
                    {
                        loadmore = false;
                    }
                }
            }
			else
			{
				nointernet();
            }
        }
    }
    private class loadplaylistvideos extends AsyncTask<Void, Void, Void> {
        boolean isconnect = false;

        @Override
        protected void onPreExecute() {
            // Showing progress dialog before sending http request.
            if (!loadmore)
            {
                progress = GoogleProgress.Progressshow(VideoList.this);
                progress.show();
            }
        }

        protected Void doInBackground(Void... unused) {

            try
            {
                if(!connectionDetector.isConnectingToInternet())
                {
                    nointernet();
                }
                else
                {

                    HttpClient client = new DefaultHttpClient();
                    HttpConnectionParams.setConnectionTimeout(client.getParams(),
                            15000);
                    HttpConnectionParams.setSoTimeout(client.getParams(), 15000);

                    if (!loadmore)
                    {
                        YOUTUBE_URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="
                                + CHANNEL_ID
                                + "&maxResults="
                                + DataManager.maxResults + "&key=" + mFirebaseRemoteConfig.getString(ANDROID_YOUTUBE_API_KEY)+"&order=date";  // USE  Remote Config API Key
                    }
                    else
                    {
                        YOUTUBE_URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&pageToken="
                                + NEXT_PAGE_TOKEN
                                + "&playlistId="
                                + CHANNEL_ID
                                + "&maxResults="
                                + DataManager.maxResults
                                + "&key="
                                + mFirebaseRemoteConfig.getString(ANDROID_YOUTUBE_API_KEY)+"&order=date";    // USE  Remote Config API Key
                    }

                    HttpUriRequest request = new HttpGet(YOUTUBE_URL);

                    HttpResponse response = client.execute(request);

                    InputStream atomInputStream = response.getEntity().getContent();

                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            atomInputStream));

                    String line;
                    String str = "";
                    while ((line = in.readLine()) != null) {
                        str += line;
                    }

                   Log.d("url---","URL" + YOUTUBE_URL);

                    JSONObject json = new JSONObject(str);
                    JSONArray items = json.getJSONArray("items");

                    Log.d("JSONARRAY---","JAONARRAY DATA " + items);

                    total = json.getJSONObject("pageInfo").getInt("totalResults");
                    Log.d("Total---","Total DATA " + total);
                    if (total > 10)
                    {

                       if(json.has("nextPageToken"))
                       {
                           loadmore = true;
                            NEXT_PAGE_TOKEN = json.getString("nextPageToken");

                        }

                    }

                    for (int i = 0; i < items.length(); i++)
                    {

                        VideoPojo video = new VideoPojo();
                        JSONObject youtubeObject = items.getJSONObject(i).getJSONObject("snippet");

                        Log.d("VIDEO ID---","VIDEO ID" + youtubeObject.getJSONObject("resourceId").getString("videoId"));
                        if (youtubeObject.getJSONObject("resourceId").getString("videoId") != null) {

                            video.setVideoid(youtubeObject.getJSONObject("resourceId").getString("videoId"));
                            video.setTitle(youtubeObject.getString("title"));
                            video.setThumbnail(youtubeObject.getJSONObject("thumbnails").getJSONObject("high").getString("url"));

                            videolist.add(video);

                        }

                    }

                    isconnect = true;
                }
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                isconnect = false;
                System.out.println(" 1exception---" + e.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                isconnect = false;
                System.out.println("2 exception---" + e.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                System.out.println("3 exception---" + e.getMessage());
                e.printStackTrace();
                //isconnect = false;
            }

            return (null);
        }

        protected void onPostExecute(Void unused) {
            // Closing progress dialog.
            progress.dismiss();
            if (isconnect)
            {
                if (videolist.size() > 0)
                {

                    Log.d("Video/list Size:-----","VideoListSize:---"+videolist.size());
                    displayInterstitial();
                    adapter = new Custom_Adapter(getApplicationContext());
                    lvvideos.setAdapter(adapter);

                    if (loadmore)
                        lvvideos.setSelection(((videolist.size() - DataManager.maxResults)-1));
                    else
                        lvvideos.setSelection(0);


                    if (total > videolist.size())
                    {
                        loadmore = true;
                    }
                    else
                    {
                        loadmore = false;
                    }
                }
            }
            else
            {
                nointernet();
            }
        }
    }

    public class Custom_Adapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public Custom_Adapter(Context c) {
            mInflater = LayoutInflater.from(c);
            imageLoader = getImageLoader(c);
        }

        @Override
        public int getCount() {
            return videolist.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.row_video_list, null);

                holder = new ViewHolder();

                holder.txttitle = (TextView) convertView
                        .findViewById(R.id.txttitle);

                holder.img = (FeedImageView) convertView.findViewById(R.id.img);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txttitle.setText("" + videolist.get(position).getTitle());

            holder.img.setImageUrl(videolist.get(position).getThumbnail(),
                    imageLoader);

            return convertView;
        }

        class ViewHolder {
            TextView txttitle;
            FeedImageView img;

        }

    }

    public RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader(Context context) {
        getRequestQueue(context);
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }
}
