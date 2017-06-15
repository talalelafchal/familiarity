package com.xsota.twittergps.twittergps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class MapsActivity extends FragmentActivity implements LocationListener {

  private GoogleMap map;

  private LocationManager locationManager;

  private Twitter twitter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);
    setUpMapIfNeeded();
    locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

    twitter = new TwitterFactory().getSingleton();
    twitter.setOAuthConsumer("api key", "api secret");
    twitter.setOAuthAccessToken(new AccessToken("token", "token secret"));
  }

  @Override
  protected void onResume() {
    super.onResume();
    setUpMapIfNeeded();

    if(Build.VERSION.SDK_INT >= 23) {
      if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        //permissionがなかった時の処理
        return;
      }
    }

    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600, 0, this);
    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 600, 0, this);
  }

  private void setUpMapIfNeeded() {
    if (map == null) {
      map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
          .getMap();
    }
  }


  private void getTweets(Location location) throws TwitterException {
    Query query = new Query();

    // 緯度経度セットする
    GeoLocation geo = new GeoLocation(location.getLatitude(),location.getLongitude());
    query.setGeoCode(geo, 10.0, Query.KILOMETERS);

    AsyncTask<Query, Void, QueryResult> task = new AsyncTask<Query, Void, QueryResult>() {

      @Override
      protected QueryResult doInBackground(Query... params) {

        Query query = params[0];
        // 検索する
        QueryResult result = null;
        try {
          result = twitter.search(query);
        } catch (TwitterException e) {
          e.printStackTrace();
        }

        return result;
      }

      //検索結果をマーカーとして表示
      @Override
      protected void onPostExecute(QueryResult result) {

        for(twitter4j.Status status : result.getTweets()){
          Log.i("text", status.getText());
          Log.i("place",status.getPlace() + " : " + status.getGeoLocation());
          GeoLocation geoLocation = status.getGeoLocation();

          if(geoLocation == null){
            return;
          }
          map.addMarker(new MarkerOptions().position(new LatLng(geoLocation.getLatitude(), geoLocation.getLongitude())).title("Tweet"));
        }


        return;
      }

    }.execute(query);


  }

  @Override
  public void onLocationChanged(Location location) {
    Log.i("lonlat:", "lon:" + location.getLongitude());
    Log.i("lonlat:", "lat:" + location.getLatitude());

    LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
    map.animateCamera(CameraUpdateFactory.newLatLng(latlng));

    try {
      getTweets(location);
    } catch (TwitterException e) {
      Log.e("TwitterException",e.getMessage());
    }
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {

  }

  @Override
  public void onProviderEnabled(String provider) {

  }

  @Override
  public void onProviderDisabled(String provider) {

  }

}
