package com.barkhappy;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.barkhappy.fragments.POIDetailsDialogFragment;
import com.barkhappy.model.LocalUser;
import com.barkhappy.model.PointOfInterest;
import com.barkhappy.utils.Constant;
import com.barkhappy.utils.FontUtils;
import com.barkhappy.utils.Fonts;
import com.barkhappy.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MapActivity extends Activity
{
    @InjectView(R.id.privacy_flip_image_button) Button privacyFlipButton;

    private MapFragment googleMapFragment;
    private GoogleMap map;
    private Location currentUserLocation;
    private LocationManager locationManager;

    private HashMap<String, Marker> markerEfficiencyTracker = new HashMap<>();
    private HashMap<Marker, PointOfInterest> markerTracker = new HashMap<>();
    public static final int MAX_MARKERS = 200, MARKER_QUERY_DELAY = 2000;

    private boolean hasMostRecentData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.inject(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        googleMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        map = googleMapFragment.getMap();
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        checkCachedUserPosition();
        startLocationUpdate();
        startPOIPoll();
        initPOIInfoWindow();
        fetchMostRecentUserData();
    }

    public void startLocationUpdate()
    {
        LocationListener locationListener = new LocationListener()
        {
            public void onLocationChanged(Location userLocation)
            {
                Utils.log("Location Changed to: " + userLocation.getLatitude() + ", " + userLocation.getLongitude());
                if (currentUserLocation == null)
                {
                    updateMapForCurrentUserPosition(userLocation);
                }

                currentUserLocation = userLocation;
                fetchPointsOfInterest();
            }

            public void onStatusChanged(String provider, int status, Bundle extras)
            {
            }

            public void onProviderEnabled(String provider)
            {
            }

            public void onProviderDisabled(String provider)
            {
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 20, locationListener);
    }

    public void checkCachedUserPosition()
    {
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        currentUserLocation = lastKnownLocation;
        updateMapForCurrentUserPosition(currentUserLocation);
    }

    public void updateMapForCurrentUserPosition(Location userLocation)
    {
        LocalUser.getInstance().setCurrentLocation(userLocation);

        if (userLocation != null)
        {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.getLatitude(),
                    userLocation.getLongitude()), 13.0f));
        }
    }

    public void startPOIPoll()
    {
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener()
        {
            private long lastPollTime = 0;

            @Override
            public void onCameraChange(CameraPosition cameraPosition)
            {
                if (System.currentTimeMillis() - lastPollTime > MARKER_QUERY_DELAY)
                {
                    fetchPointsOfInterest();
                    lastPollTime = System.currentTimeMillis();
                }
            }
        });
    }

    public void fetchPointsOfInterest()
    {
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        LatLng southWest = bounds.southwest;
        LatLng northEast = bounds.northeast;

        ParseQuery<PointOfInterest> pointOfInterestQuery = new ParseQuery<PointOfInterest>("PointofIntrest");
        pointOfInterestQuery.whereWithinGeoBox("GeoLocation", new ParseGeoPoint(southWest.latitude, southWest.longitude),
                new ParseGeoPoint(northEast.latitude, northEast.longitude));
        pointOfInterestQuery.findInBackground(new FindCallback<PointOfInterest>()
        {
            @Override
            public void done(List<PointOfInterest> parseObjects, ParseException e)
            {
                if (e == null)
                {
                    if (markerEfficiencyTracker.keySet().size() > MAX_MARKERS)
                    {
                        markerEfficiencyTracker.clear();
                        map.clear();
                    }

                    Utils.log("Received " + parseObjects.size() + " pois");
                    for (PointOfInterest poi : parseObjects)
                    {
                        if (markerEfficiencyTracker.get(poi.getObjectId()) == null)
                        {
                            ParseGeoPoint poiLoc = (ParseGeoPoint) poi.get("GeoLocation");
                            int categoryTag = poi.getInt("CategoryTag");

                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(poiLoc.getLatitude(), poiLoc.getLongitude()));
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(Constant.MAP_MARKERS[categoryTag]));
                            Marker freshMarker = map.addMarker(markerOptions);
                            markerEfficiencyTracker.put(poi.getObjectId(), freshMarker);
                            markerTracker.put(freshMarker, poi);
                        }
                    }
                }
            }
        });
    }

    public void initPOIInfoWindow()
    {
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter()
        {
            @Override
            public View getInfoWindow(Marker marker)
            {
                ParseObject poiDescriptor = markerTracker.get(marker);
                String poiName = poiDescriptor.getString("Name");

                View rootView = View.inflate(MapActivity.this, R.layout.basic_poi_info_window_layout, null);
                TextView descriptionTextView = (TextView) rootView.findViewById(R.id.basic_info_title_text_view);
                descriptionTextView.setText(poiName);
                FontUtils.getInstance().overrideFonts(rootView, Fonts.LIGHT);

                ImageView poiTypeImageView = (ImageView) rootView.findViewById(R.id.basic_info_window_type_image_view);
                poiTypeImageView.setImageResource(Constant.POI_AVATAR_INDEX[poiDescriptor.getInt("CategoryTag")]);
                return rootView;
            }

            @Override
            public View getInfoContents(Marker marker)
            {
                return null;
            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
        {
            @Override
            public void onInfoWindowClick(Marker marker)
            {
                POIDetailsDialogFragment detailsDialogFragment = POIDetailsDialogFragment.newInstance(markerTracker.get(marker));
                detailsDialogFragment.show(getFragmentManager(), "poi_details_dialog");
            }
        });
    }

    @OnClick(R.id.my_location_image_button)
    public void myLocationClicked()
    {
        if (currentUserLocation != null)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude()), 17));
    }

    public void fetchMostRecentUserData()
    {
        ParseUser.getCurrentUser().getParseObject("userInfoPointer").fetchInBackground(new GetCallback<ParseObject>()
        {
            @Override
            public void done(ParseObject parseObject, ParseException e)
            {
                if (e == null)
                {
                    hasMostRecentData = true;
                    updateOnlineButtonState();
                }
            }
        });
    }

    @OnClick(R.id.privacy_flip_image_button)
    public void onPrivacyFlipButtonClicked()
    {
        if (hasMostRecentData)
        {
            boolean isOnline = !getUserState();
            ParseUser.getCurrentUser().getParseObject("userInfoPointer").put("online", isOnline);
            ParseUser.getCurrentUser().saveInBackground();
            updateOnlineButtonState();

            if (isOnline)
            {
                Toast.makeText(this, R.string.profile_visible, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, R.string.profile_invisible, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateOnlineButtonState()
    {
        if (hasMostRecentData)
        {
            if (getUserState())
            {
                privacyFlipButton.setBackgroundResource(R.drawable.map_switch_on2x);
            }
            else
            {
                privacyFlipButton.setBackgroundResource(R.drawable.map_switch_off2x);
            }
        }
    }

    public boolean getUserState()
    {
        return ParseUser.getCurrentUser().getParseObject("userInfoPointer").getBoolean("online");
    }
}
