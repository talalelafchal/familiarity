package com.eventify.android.fragments;
 
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
 
import com.eventify.android.R;
import com.eventify.android.models.Event;
import com.eventify.android.utils.AsyncCallback;
import com.eventify.android.utils.LocationUtils;
import com.eventify.android.utils.PersistentSaveKeys;
import com.eventify.android.utils.managers.PinManager;
import com.eventify.android.views.MapWrapper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
 
import java.util.HashMap;
import java.util.List;
 
import butterknife.InjectView;
 
/**
 * Created by Greg on 5/24/15.
 */
public class EventMapFragment extends BaseFragment implements PersistentSaveKeys
{
    public interface OnMapEventListener
    {
        void onNewEvent(LatLng loc);
    }
 
    @InjectView(R.id.sliding_layout_event_details) SlidingUpPanelLayout mSlideLayout;
    @InjectView(R.id.sliding_map_wrapper) MapWrapper mWrapView;
 
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private HashMap<Marker, Event> mMarkerMap = new HashMap<>();
    private EventDetailsFragment detailsFragment;
    private OnMapEventListener mListener;
 
    public static EventMapFragment getInstance()
    {
        return new EventMapFragment();
    }
 
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mListener = (OnMapEventListener) activity;
    }
 
    @Override
    public View getRootView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }
 
    @Override
    public void onPause()
    {
        super.onPause();
        saveMapState();
    }
 
    @Override
    public void initUI()
    {
        LatLng savedLoc = loadSavedMapLocation();
        float zoom = loadZoom();
 
        if (savedLoc != null)
        {
            GoogleMapOptions options = new GoogleMapOptions()
                    .camera(CameraPosition.fromLatLngZoom(savedLoc, zoom));
            initMapWithOptions(options);
        }
        else
        {
            LocationUtils.getInstance(getActivity()).getUserLocation(new AsyncCallback<Location>()
            {
                @Override
                public void onAsyncOperationCompleted(Location result)
                {
                    GoogleMapOptions options = new GoogleMapOptions()
                            .camera(CameraPosition.fromLatLngZoom(new LatLng(result.getLatitude(),
                                    result.getLongitude()), 11.0f));
                    initMapWithOptions(options);
                }
            });
        }
 
        detailsFragment = (EventDetailsFragment) getChildFragmentManager().findFragmentById(R.id.fragment_event_details);
        mSlideLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                mSlideLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mSlideLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });
    }
 
    private void initMapWithOptions(GoogleMapOptions options)
    {
        mMapFragment = SupportMapFragment.newInstance(options);
        getChildFragmentManager().beginTransaction().replace(R.id.g_map_events_container, mMapFragment).commit();
 
        mMapFragment.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                mMap = googleMap;
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback()
                {
                    @Override
                    public void onMapLoaded()
                    {
                        EventMapFragment.this.onMapReady();
                    }
                });
            }
        });
    }
 
    private void onMapReady()
    {
        fetchPins();
 
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker)
            {
                CameraUpdate centerUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition.
                        fromLatLngZoom(marker.getPosition(), loadZoom()));
                mMap.animateCamera(centerUpdate);
                mSlideLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
 
                if (detailsFragment != null)
                {
                    Event eventData = mMarkerMap.get(marker);
                    if (eventData != null)
                        detailsFragment.updateUI(eventData);
                }
 
                return false;
            }
        });
 
        mWrapView.setPanListener(new MapWrapper.OnFinishedPanningMapListener()
        {
            @Override
            public void onFinishedPanning()
            {
                fetchPins();
                mSlideLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });
 
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {
                mListener.onNewEvent(latLng);
            }
        });
    }
 
    private void fetchPins()
    {
        LatLngBounds mVisibleRegion = mMap.getProjection().getVisibleRegion().latLngBounds;
        ParseGeoPoint southWest = new ParseGeoPoint(mVisibleRegion.southwest.latitude,
                mVisibleRegion.southwest.longitude);
        ParseGeoPoint northEast = new ParseGeoPoint(mVisibleRegion.northeast.latitude,
                mVisibleRegion.northeast.longitude);
 
        Event.getQuery()
                .include("type")
                .whereWithinGeoBox("location", southWest, northEast)
                .findInBackground(new FindCallback<Event>()
                {
                    @Override
                    public void done(List<Event> list, ParseException e)
                    {
                        if (e == null)
                        {
                            if (list != null)
                                for (Event event : list)
                                    addPinToMap(event);
                        }
                        else
                        {
                            e.printStackTrace();
                        }
                    }
                });
    }
 
    private void addPinToMap(Event event)
    {
        if (mMap != null)
        {
            Bitmap pinIcon = PinManager.getInstance().getColoredPin(event, getActivity());
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(pinIcon);
            MarkerOptions options = new MarkerOptions()
                    .position(event.getLocation())
                    .icon(descriptor);
 
            Marker marker = mMap.addMarker(options);
            mMarkerMap.put(marker, event);
        }
    }
 
    private void saveMapState()
    {
        if (mMap != null)
        {
            LatLng mapCenter = mMap.getCameraPosition().target;
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
            editor.putFloat(USER_LOCATION_LAT, (float) mapCenter.latitude).apply();
            editor.putFloat(USER_LOCATION_LON, (float) mapCenter.longitude).apply();
            editor.putFloat(USER_ZOOM, mMap.getCameraPosition().zoom).apply();
        }
    }
 
    private LatLng loadSavedMapLocation()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        float lat = prefs.getFloat(USER_LOCATION_LAT, Float.MIN_VALUE);
        float lon = prefs.getFloat(USER_LOCATION_LON, Float.MIN_VALUE);
        if (lat != Float.MIN_VALUE && lon != Float.MIN_VALUE)
        {
            return new LatLng(lat, lon);
        }
 
        return null;
    }
 
    private float loadZoom()
    {
        return PreferenceManager.getDefaultSharedPreferences(getActivity()).getFloat(USER_ZOOM, 11.0f);
    }
}