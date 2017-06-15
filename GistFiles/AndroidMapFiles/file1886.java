package com.barkhappy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.barkhappy.model.ParseDogInfo;
import com.barkhappy.parse.ImageLoader;
import com.barkhappy.parse.NearByDogsListViewAdapter;
import com.barkhappy.profile.creation.DogProfileViewActivity;
import com.barkhappy.utils.FontUtils;
import com.barkhappy.utils.Fonts;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NearbyDogsActivity extends Activity implements
        View.OnClickListener
{

    private Button btn_dogs_like;
    private Button btn_bytemperament;
    private Button btn_bybreed;
    private Button btn_bysize;
    private ImageButton btn_close;
    ProgressDialog mProgressDialog;
    private List<ParseDogInfo> lstDogs = null;
    ImageLoader imageLoader;
    List<ParseObject> lstNearByUsers;
    List<ParseObject> lstNearByDogs;
    List<ParseObject> lst = new ArrayList<ParseObject>();
    private double distance = 50.0;
    private String searchKey = "";
    private ParseObject curUserInfo;
    public static ParseGeoPoint userLocation;

    ListView listview;
    NearByDogsListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_dogs);

        btn_close = (ImageButton) findViewById(R.id.nearby_btn_dogs_close);
        btn_close.setOnClickListener(this);

        btn_dogs_like = (Button) findViewById(R.id.nearby_btn_dogs_like);
        btn_dogs_like.setOnClickListener(this);

        btn_bytemperament = (Button) findViewById(R.id.nearby_btn_bytemperament);
        btn_bytemperament.setOnClickListener(this);

        btn_bybreed = (Button) findViewById(R.id.nearby_btn_bybreed);
        btn_bybreed.setOnClickListener(this);

        btn_bysize = (Button) findViewById(R.id.nearby_btn_bysize);
        btn_bysize.setOnClickListener(this);

        FontUtils.getInstance().overrideFonts(findViewById(R.id.layout_nearby_dogs), Fonts.LIGHT);

        curUserInfo = DogProfileViewActivity.ob_user.get(0);
        userLocation = (ParseGeoPoint) curUserInfo.get("geoLocation");
        new NearByDogsTask().execute();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        int id = v.getId();

        if (id == R.id.nearby_btn_dogs_close)
        {
            this.finish();
            this.overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);
        }
        else
        {
            if (id == R.id.nearby_btn_dogs_like)
            {
                btn_dogs_like.setBackgroundDrawable(getResources().getDrawable(R.drawable.dp_option_done2x));
                btn_dogs_like.setTextColor(getResources().getColor(R.color.white));

                btn_bytemperament.setBackgroundDrawable(getResources().getDrawable(R.drawable.poi_button_blank_half2x));
                btn_bytemperament.setTextColor(getResources().getColor(R.color.blue_button));
                btn_bybreed.setBackgroundDrawable(getResources().getDrawable(R.drawable.poi_button_blank_half2x));
                btn_bybreed.setTextColor(getResources().getColor(R.color.blue_button));
                btn_bysize.setBackgroundDrawable(getResources().getDrawable(R.drawable.poi_button_blank_half2x));
                btn_bysize.setTextColor(getResources().getColor(R.color.blue_button));
                searchKey = "like";
                new NearByDogsTask().execute();

            }
            else if (id == R.id.nearby_btn_bytemperament)
            {
                btn_bytemperament.setBackgroundDrawable(getResources().getDrawable(R.drawable.dp_option_done2x));
                btn_bytemperament.setTextColor(getResources().getColor(R.color.white));
                btn_dogs_like.setBackgroundDrawable(getResources().getDrawable(R.drawable.poi_button_blank_half2x));
                btn_dogs_like.setTextColor(getResources().getColor(R.color.blue_button));
                btn_bybreed.setBackgroundDrawable(getResources().getDrawable(R.drawable.poi_button_blank_half2x));
                btn_bybreed.setTextColor(getResources().getColor(R.color.blue_button));
                btn_bysize.setBackgroundDrawable(getResources().getDrawable(R.drawable.poi_button_blank_half2x));
                btn_bysize.setTextColor(getResources().getColor(R.color.blue_button));
                searchKey = "temperament";
                new NearByDogsTask().execute();

            }
            else if (id == R.id.nearby_btn_bybreed)
            {
                btn_bybreed.setBackgroundDrawable(getResources().getDrawable(R.drawable.dp_option_done2x));
                btn_bybreed.setTextColor(getResources().getColor(R.color.white));

                btn_bytemperament.setBackgroundDrawable(getResources()
                        .getDrawable(R.drawable.poi_button_blank_half2x));
                btn_bytemperament.setTextColor(getResources().getColor(R.color.blue_button));
                btn_dogs_like.setBackgroundDrawable(getResources().getDrawable(R.drawable.poi_button_blank_half2x));
                btn_dogs_like.setTextColor(getResources().getColor(R.color.blue_button));
                btn_bysize.setBackgroundDrawable(getResources().getDrawable(R.drawable.poi_button_blank_half2x));
                btn_bysize.setTextColor(getResources().getColor(R.color.blue_button));
                searchKey = "breed";
                new NearByDogsTask().execute();

            }
            else if (id == R.id.nearby_btn_bysize)
            {
                btn_bysize.setBackgroundDrawable(getResources().getDrawable(R.drawable.dp_option_done2x));
                btn_bysize.setTextColor(getResources().getColor(R.color.white));

                btn_bytemperament.setBackgroundDrawable(getResources().getDrawable(R.drawable.poi_button_blank_half2x));
                btn_bytemperament.setTextColor(getResources().getColor(R.color.blue_button));
                btn_bybreed.setBackgroundDrawable(getResources().getDrawable(R.drawable.poi_button_blank_half2x));
                btn_bybreed.setTextColor(getResources().getColor(R.color.blue_button));
                btn_dogs_like.setBackgroundDrawable(getResources().getDrawable(R.drawable.poi_button_blank_half2x));
                btn_dogs_like.setTextColor(getResources().getColor(R.color.blue_button));

                searchKey = "size";
                new NearByDogsTask().execute();
            }
        }
    }

    private class NearByDogsTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(NearbyDogsActivity.this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            // Create the array

            lstNearByDogs = new ArrayList<ParseObject>();

            ParseQuery<ParseObject> query_user = new ParseQuery<ParseObject>("UserInfo")
                    .whereWithinKilometers("geoLocation", userLocation, distance).whereNotEqualTo("objectId", curUserInfo.getObjectId());
            query_user.whereEqualTo("online", true);
            Date dt = new Date();
            long time_before = dt.getTime() - 3600 * 6 * 1000;
            dt = new Date(time_before);
            query_user.whereGreaterThanOrEqualTo("lastLocationUpdateTime", dt);
            ParseQuery<ParseObject> query_dog = new ParseQuery<ParseObject>("DogInfo").whereMatchesKeyInQuery("owner", "objectId", query_user);
            query_dog.include("owner");

            //query_dog.whereGreaterThanOrEqualTo("lastLocationUpdateTime", dt);
            if (searchKey.equals("like"))
            {
                ParseObject myDog = DogProfileViewActivity.ob_dog.get(0);
                String myDogGetAlong = myDog.get("getAlong").toString();
                List<String> lstSearchKey = new ArrayList<String>();

                if (myDogGetAlong.equals("All Dogs"))
                {
                    lstSearchKey.add("Huge");
                    lstSearchKey.add("Large");
                    lstSearchKey.add("Medium");
                    lstSearchKey.add("Small");
                    lstSearchKey.add("");
                    query_dog.whereContainedIn("size", lstSearchKey);
                    //likeSize = @[@"Huge", @"Large", @"Medium", @"Small", @""];
                }
                else if (myDogGetAlong.equals("Large Dogs only"))
                {
                    lstSearchKey.add("Huge");
                    lstSearchKey.add("Large");
                    lstSearchKey.add("Medium");
                    query_dog.whereContainedIn("size", lstSearchKey);
                    //likeSize = @[@"Huge", @"Large", @"Medium"];
                }
                else if (myDogGetAlong.equals("Small Dogs only"))
                {
                    lstSearchKey.add("Medium");
                    lstSearchKey.add("Small");
                    lstSearchKey.add("Tiny");
                    query_dog.whereContainedIn("size", lstSearchKey);
                    //likeSize = @[@"Medium", @"Small", @"Tiny"];
                }

            }
            else if (searchKey.equals("temperament"))
            {
                query_dog.addAscendingOrder("getAlong");
            }
            else if (searchKey.equals("breed"))
            {
                query_dog.addAscendingOrder("breed");
            }
            else if (searchKey.equals("size"))
            {
                query_dog.addAscendingOrder("size");
            }

            try
            {
                lst = new ArrayList<ParseObject>();
                lst = query_dog.find();

            }
            catch (ParseException e)
            {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {

            listview = (ListView) findViewById(R.id.nearby_lst_dogs);
            adapter = new NearByDogsListViewAdapter(NearbyDogsActivity.this, lst);
            listview.setAdapter(adapter);
            mProgressDialog.dismiss();
        }
    }

    public static double distFrom(ParseGeoPoint myLoc, ParseGeoPoint otherLoc)
    {
        double lat1 = myLoc.getLatitude();
        double lng1 = myLoc.getLongitude();
        double lat2 = otherLoc.getLatitude();
        double lng2 = otherLoc.getLongitude();

        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c * 1000;//Unit Meter

        return dist;
    }
}
