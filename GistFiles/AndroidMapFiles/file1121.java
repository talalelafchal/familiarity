/*
 *     Copyright 2016, 2017 IBM Corp.
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.ibm.mobile.sdkgen.weatherstarter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import com.esri.arcgisruntime.tasks.geocode.ReverseGeocodeParameters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.mobile.sdkgen.weatherstarter.model.CurrentCondition;
import com.ibm.mobile.sdkgen.weatherstarter.model.Daily;
import com.ibm.mobile.sdkgen.weatherstarter.model.DailyForecast;
import com.ibm.mobile.sdkgen.weatherstarter.model.ForecastDaily;
import com.ibm.mobile.sdkgen.weatherstarter.model.Observation;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Request;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;

import org.json.JSONObject;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * Initial activity that displays and handles interactions with the world map.
 */
public class MainActivity extends AppCompatActivity {


    private MapView mMapView;
    private Callout mCallout;
    private String obsText;
    private Gson gson;
    private JsonReader reader;

    private String username;
    private String password;
    private String basePath;

    private Context mContext;

    private DailyForecast mDailyForecast;
    private CurrentCondition mCurrentCondition;

    private String cityname;
    private Point mapviewPoint;

    private View mView;
    private LayoutInflater mInflater;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esri_map);


        // Core SDK must be initialized to interact with Bluemix Mobile services.
        BMSClient.getInstance().initialize(getApplicationContext(), BMSClient.REGION_US_SOUTH);

        mContext = getApplicationContext();

        basePath = "https://" + mContext.getResources().getString(R.string.weather_host) + "/api/weather";
        username = mContext.getResources().getString(R.string.weather_username);
        password = mContext.getResources().getString(R.string.weather_password);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        gson = gsonBuilder.create();


        mMapView = (MapView) findViewById(R.id.mapView);
        ArcGISMap map = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 34.056295, -117.195800, 16);
        mMapView.setMap(map);
        
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public void onLongPress(MotionEvent view) {

                // get the point that was clicked and convert it to a point in map coordinates
                android.graphics.Point screenPoint = new android.graphics.Point(Math.round(view.getX()),
                        Math.round(view.getY()));

                mapviewPoint = mMapView.screenToLocation(screenPoint);

                // Geographic coordinates are needed...
                Point mapPoint = (Point)GeometryEngine.project(mapviewPoint, SpatialReference.create(4326));


                mMapView.getCallout().dismiss();


                // Geocode
                findLocation(mapPoint);



            }
        });


        this.setTitle("Weather");
    }

    private void findLocation(final Point mapPoint) {
        // Create a LocatorTask using an online locator
        final LocatorTask onlineLocator = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");


        onlineLocator.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (onlineLocator.getLoadStatus() == LoadStatus.LOADED) {
                    // Locator is ready to use
                    cityname = "";
                    ReverseGeocodeParameters params = new ReverseGeocodeParameters();
                    params.setOutputSpatialReference(mapPoint.getSpatialReference());
                    params.setMaxDistance(500.0);
                    params.setMaxResults(5);

                    final ListenableFuture<List<GeocodeResult>> result = onlineLocator.reverseGeocodeAsync(mapPoint, params);
                    result.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<GeocodeResult> results = result.get();
                                if (results.size() > 0) {
                                    // Use the first result
                                    GeocodeResult topResult = results.get(0);
                                    Map<String, Object> atts = topResult.getAttributes();
                                    cityname = (String)atts.get("City");

                                    // Call The Weather Company APIs for observations
                                    setObservations(mapPoint);


                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                        });


                }
            }
        });
        onlineLocator.loadAsync();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void setObservations(final Point coordinate) {

        mInflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        mView = mInflater.inflate(R.layout.current_conditions_info, null);

        String path2 = "/v1/geocode/{latitude}/{longitude}/observations.json"
                .replaceAll("\\{latitude\\}", ((Double) coordinate.getY()).toString())
                .replaceAll("\\{longitude\\}", ((Double) coordinate.getX()).toString());

        String basicAuthString = username + ":" + password;
        byte[] encodedString = Base64.encode(basicAuthString.getBytes(), Base64.NO_WRAP);
        String credentials = "Basic " + new String(encodedString);
        Map<String, List<String>> headers = Collections.singletonMap("Authorization", Collections.singletonList(credentials));
        Map<String, String> queryParameters = Collections.singletonMap("language", "en-US");

        try {
            Request request = new Request(this.basePath + path2, Request.GET);
            request.setQueryParameters(queryParameters);
            request.setHeaders(headers);
            request.send(mContext, new ResponseListener() {

                @Override
                public void onSuccess(Response response) {

                    obsText = response.getResponseText();

                    System.out.println(obsText);

                    reader = new JsonReader(new StringReader(obsText));

                    mCurrentCondition = gson.fromJson(response.getResponseText(), CurrentCondition.class);
                    Observation observation = mCurrentCondition.getObservation();


                    final ImageView currentIcon = (ImageView) mView.findViewById(R.id.icon);
                    final TextView locality = (TextView) mView.findViewById(R.id.locality);

                    final TextView description = (TextView) mView.findViewById(R.id.description);
                    final TextView currentTemp = (TextView) mView.findViewById(R.id.temp);
                    final TextView feelsLike = (TextView) mView.findViewById(R.id.feels_like);
                    final TextView humidity = (TextView) mView.findViewById(R.id.humidity);
                    final TextView wind = (TextView) mView.findViewById(R.id.wind);

                    //description.setText(observation.getBluntPhrase());
                    currentTemp.setText(observation.getTemp() + "\u00B0");
                    feelsLike.setText(observation.getFeelsLike() + "\u00B0");
                    humidity.setText(observation.getRh() + "%");
                    wind.setText(observation.getWspd() + "mph " + observation.getWdirCardinal());

                    Drawable drawable = null;
                    Integer iconCode = observation.getWxIcon();
                    iconCode = (iconCode == null) ? 44 : iconCode;
                    int id = mContext.getResources().getIdentifier("ic_" + iconCode, "drawable", mContext.getPackageName());

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        drawable = mContext.getDrawable(id);
                    } else {
                        drawable = mContext.getResources().getDrawable(id);
                    }
                    currentIcon.setImageDrawable(drawable);


                    setThreeDayForecast(coordinate);


                }

                @Override
                public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setThreeDayForecast(final Point coordinate) {


        String path = "/v1/geocode/{latitude}/{longitude}/forecast/daily/3day.json"
                .replaceAll("\\{latitude\\}", ((Double) coordinate.getY() ).toString())
                .replaceAll("\\{longitude\\}", ((Double) coordinate.getX()).toString());


        String basicAuthString = username + ":" + password;
        byte[] encodedString = Base64.encode(basicAuthString.getBytes(), Base64.NO_WRAP);
        String credentials = "Basic " + new String(encodedString);
        Map<String, List<String>> headers = Collections.singletonMap("Authorization", Collections.singletonList(credentials));
        Map<String, String> queryParameters = Collections.singletonMap("language", "en-US");


        try {
            Request request = new Request(this.basePath + path, Request.GET);
            request.setQueryParameters(queryParameters);
            request.setHeaders(headers);
            request.send(mContext, new ResponseListener() {

                @Override
                public void onSuccess(Response response) {
                    final String text = response.getResponseText();

                    System.out.println(text);

                    reader = new JsonReader(new StringReader(text));


                    final ImageView currentIcon = (ImageView) mView.findViewById(R.id.icon);
                    final TextView locality = (TextView) mView.findViewById(R.id.locality);

                    final TextView description = (TextView) mView.findViewById(R.id.description);
                    final TextView currentTemp = (TextView) mView.findViewById(R.id.temp);
                    final TextView feelsLike = (TextView) mView.findViewById(R.id.feels_like);
                    final TextView humidity = (TextView) mView.findViewById(R.id.humidity);
                    final TextView wind = (TextView) mView.findViewById(R.id.wind);

                    final TextView narrative = (TextView) mView.findViewById(R.id.narrative);
                    final TextView today = (TextView) mView.findViewById(R.id.today);

                    final ImageView dayOneIcon = (ImageView) mView.findViewById(R.id.icon_one);
                    final ImageView dayTwoIcon = (ImageView) mView.findViewById(R.id.icon_two);
                    final ImageView dayThreeIcon = (ImageView) mView.findViewById(R.id.icon_three);

                    final TextView dayOne = (TextView) mView.findViewById(R.id.day_one);
                    final TextView dayTwo = (TextView) mView.findViewById(R.id.day_two);
                    final TextView dayThree = (TextView) mView.findViewById(R.id.day_three);
                    final TextView dayOneTemp = (TextView) mView.findViewById(R.id.temp_one);
                    final TextView dayTwoTemp = (TextView) mView.findViewById(R.id.temp_two);
                    final TextView dayThreeTemp = (TextView) mView.findViewById(R.id.temp_three);

                    mCurrentCondition = gson.fromJson(text, CurrentCondition.class);
                    Observation observation = mCurrentCondition.getObservation();
                    if(observation != null) {
                        currentTemp.setText(observation.getTemp() + "\u00B0");
                    }

                    mDailyForecast = gson.fromJson(text, DailyForecast.class);
                    List<ForecastDaily> forecasts = mDailyForecast.getForecasts();
                    Daily daily = (forecasts.get(0).getDay() == null) ? forecasts.get(0).getNight() : forecasts.get(0).getDay();
                    locality.setText(cityname);

                    narrative.setText(forecasts.get(0).getNarrative());
                    today.setText(daily.getDaypartName());
                    dayOne.setText(forecasts.get(1).getDow());
                    dayTwo.setText(forecasts.get(2).getDow());
                    dayThree.setText(forecasts.get(3).getDow());
                    dayOneTemp.setText(forecasts.get(1).getMaxTemp() + "\u00B0" + " | " + forecasts.get(0).getMinTemp() + "\u00B0");
                    dayTwoTemp.setText(forecasts.get(2).getMaxTemp() + "\u00B0" + " | " + forecasts.get(1).getMinTemp() + "\u00B0");
                    dayThreeTemp.setText(forecasts.get(3).getMaxTemp() + "\u00B0" + " | " + forecasts.get(2).getMinTemp() + "\u00B0");

                    Drawable drawable = null;
                    int id = mContext.getResources().getIdentifier("ic_" + forecasts.get(1).getDay().getIconCode(), "drawable", mContext.getPackageName());

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        drawable = mContext.getDrawable(id);
                    } else {
                        drawable = mContext.getResources().getDrawable(id);
                    }
                    dayOneIcon.setImageDrawable(drawable);

                    id = mContext.getResources().getIdentifier("ic_" + forecasts.get(2).getDay().getIconCode(), "drawable", mContext.getPackageName());

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        drawable = mContext.getDrawable(id);
                    } else {
                        drawable = mContext.getResources().getDrawable(id);
                    }
                    dayTwoIcon.setImageDrawable(drawable);

                    id = mContext.getResources().getIdentifier("ic_" + forecasts.get(3).getDay().getIconCode(), "drawable", mContext.getPackageName());

                   if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        drawable = mContext.getDrawable(id);
                    } else {
                        drawable = mContext.getResources().getDrawable(id);
                   }
                    dayThreeIcon.setImageDrawable(drawable);


                   runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //marker.showInfoWindow();
                            TextView calloutContent = new TextView(mContext);
                            calloutContent.setTextColor(Color.BLACK);
                            calloutContent.setHorizontallyScrolling(true);
                            // format coordinates to 4 decimal places
                            calloutContent.setText("Lat: " +  String.format("%.4f", coordinate.getY()) +
                                    ", Lon: " + String.format("%.4f", coordinate.getX()));

                            // get callout, set content and show
                            mCallout = mMapView.getCallout();
                            mCallout.setLocation(coordinate);
                            mCallout.setContent(mView);
                            mCallout.show();

                            // center on tapped point
                            mMapView.setViewpointCenterAsync(coordinate);



                        }
                    });
               }

                @Override
                public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                    if (response.getStatus() == 401) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                        builder.setMessage("Failed to connect to the Weather Company Data service due to invalid " +
                                "credentials. Please verify your credentials in the weather_credentials.xml file and " +
                                "rebuild the application. See the README for further assistance.");
                        builder.setTitle("Uh Oh!");
                        builder.setCancelable(false);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });
                    } else if (response.getStatus() == 400) {

                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
