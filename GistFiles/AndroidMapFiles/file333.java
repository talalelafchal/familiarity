package com.aearagi.underweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.common.GeoCoordinate;
import com.here.android.common.GeoPosition;
import com.here.android.common.Image;
import com.here.android.common.LocationMethod;
import com.here.android.common.LocationStatus;
import com.here.android.common.PositionListener;
import com.here.android.common.PositioningManager;
import com.here.android.common.ViewObject;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapGestureListener;
import com.here.android.mapping.MapMarker;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import exception.MyExceptionHandler;
import model.Location;
import model.SearchLocation;
import provider.LocationLoader;
import request.RequestFactory;

/**
 * Created by Krzysztof on 04.03.14.
 */
public class SearchLocationActivity extends ActionBarActivity {

    private String TAG = SearchLocationActivity.class.getSimpleName();

    private static final double DEF_LAT = 51.508;
    private static final double DEF_LON = -0.125;

    private static final String LOCATION_IF = "location-if";

    private String requestUrl;
    private final int LOADER_ID = 1;
    private PositioningManager mPositioningManager;

    private ListView mLocationList;
    private TextView mEmptyList;
    private ArrayAdapter<SearchLocation> mListAdapter;

    private RelativeLayout mRootView;
    private ProgressBar mProgressBar;
    private FrameLayout mMapFrame;

    private Map mMap = null;
    private MapFragment mMapFragment = null;
    private boolean isInitOK = false;

    private final SparseArray<MapMarker> mapMarkerList = new SparseArray<MapMarker>();

    private final SparseArray<SearchLocation> mapStation = new SparseArray<SearchLocation>();



    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getStringExtra("OK") != null) {

            } else {
                String error = intent.getStringExtra("ERROR");

                mEmptyList.setText(error);
                mLocationList.setEmptyView(mEmptyList);
                mEmptyList.setVisibility(View.VISIBLE);

                mProgressBar.setVisibility(View.GONE);
            }


        }
    };

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(LOCATION_IF));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());

        setResult(RESULT_CANCELED);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.search_location_layout);

        mRootView = (RelativeLayout) findViewById(R.id.root_search_location);
        mMapFrame = (FrameLayout) findViewById(R.id.station_map_frame);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_search_location);

        mLocationList = (ListView) findViewById(R.id.list_search_location);
        mEmptyList = (TextView) findViewById(R.id.empty_search_location);

        mListAdapter = new SearchStationListAdapter(this, new ArrayList<SearchLocation>());

        mLocationList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mLocationList.setAdapter(mListAdapter);
        mLocationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                SearchLocation locationToSave = mListAdapter.getItem(position);

                showStationSaveDialog(locationToSave);

            }
        });

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.station_map_fragment);
        mMapFragment.init(new FragmentInitListener() {
            @Override
            public void onFragmentInitializationCompleted(InitError initError) {
                if (initError == InitError.NONE) {

                    initLocation();

                    mMap = mMapFragment.getMap();

                    mMap.setCenter(
                            MapFactory.createGeoCoordinate(DEF_LAT, DEF_LON),
                            MapAnimation.NONE);

                    mMap.setZoomLevel((mMap.getMaxZoomLevel() +
                            mMap.getMinZoomLevel()) / 2);

                    mMapFragment.getMapGesture().addMapGestureListener(new MapGestureListener() {
                        @Override
                        public void onPanStart() {

                        }

                        @Override
                        public void onPanEnd() {

                        }

                        @Override
                        public void onMultiFingerManipulationStart() {

                        }

                        @Override
                        public void onMultiFingerManipulationEnd() {

                        }

                        @Override
                        public boolean onMapObjectsSelected(List<ViewObject> viewObjects) {
                            for (ViewObject viewObj : viewObjects) {

                                MapMarker selectedMapMarker = mapMarkerList.get(viewObj.hashCode());
                                SearchLocation selectedMapStation = mapStation.get(viewObj.hashCode());

                                if (selectedMapMarker != null) {

                                    GeoCoordinate geoCoordinate = selectedMapMarker.getCoordinate();

                                    selectedMapMarker.showInfoBubble();

                                    if (selectedMapStation != null) {

                                        int locationPosition = mListAdapter.getPosition(selectedMapStation);

                                        mLocationList.setItemChecked(locationPosition, true);
                                        mLocationList.smoothScrollToPosition(locationPosition);
                                    }

                                    mMap.setCenter(geoCoordinate, MapAnimation.NONE);

                                    return true;
                                }

                            }
                            // return false to allow the map to handle this callback also
                            return false;
                        }

                        @Override
                        public boolean onTapEvent(PointF pointF) {


                            return false;
                        }

                        @Override
                        public boolean onDoubleTapEvent(PointF pointF) {
                            return false;
                        }

                        @Override
                        public void onPinchLocked() {

                        }

                        @Override
                        public boolean onPinchZoomEvent(float v, PointF pointF) {
                            return false;
                        }

                        @Override
                        public void onRotateLocked() {

                        }

                        @Override
                        public boolean onRotateEvent(float v) {
                            return false;
                        }

                        @Override
                        public boolean onTiltEvent(float v) {
                            return false;
                        }

                        @Override
                        public boolean onLongPressEvent(PointF pointF) {
                            return false;
                        }

                        @Override
                        public void onLongPressRelease() {

                        }

                        @Override
                        public boolean onTwoFingerTapEvent(PointF pointF) {
                            return false;
                        }
                    });

                } else {

                    Toast.makeText(SearchLocationActivity.this, "ERROR: Cannot initialize Map Fragment", Toast.LENGTH_LONG).show();

                    //Log.e(TAG, "ERROR: Cannot initialize Map Fragment " + initError.name());
                }
            }
        });

        handleIntent(getIntent());

    }

    private void showStationSaveDialog(final SearchLocation locationToSave) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you wanna save this station: " + locationToSave.getCity());

        builder.setPositiveButton(
                getResources().getText(R.string.save_title),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveLocation(locationToSave);
                        finish();
                    }
                }
        );

        builder.setNegativeButton(
                getResources().getText(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );

        builder.create().show();
    }


    private void saveLocation(SearchLocation location) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(SearchLocation.COLUMN_CITY, location.getCity());
        contentValues.put(SearchLocation.COLUMN_COUNTRY, location.getCountry());
        contentValues.put(SearchLocation.COLUMN_STATE, location.getState());
        contentValues.put(SearchLocation.COLUMN_LAT, location.getLat());
        contentValues.put(SearchLocation.COLUMN_LON, location.getLon());

        if (!location.getL().equals("")) {
            contentValues.put(SearchLocation.COLUMN_TYPE, "");
            contentValues.put(SearchLocation.COLUMN_L, location.getL() + ".json");

        }

        getContentResolver().insert(SearchLocation.CONTENT_URI, contentValues);

    }

    private void initLocation() {

        mPositioningManager = MapFactory.getPositioningManager();

        LocationStatus status = mPositioningManager.getLocationStatus(LocationMethod.GPS);

        if (status == LocationStatus.OUT_OF_SERVICE) {
            showEnableGPSDialog();
        } else {

            mPositioningManager.start(LocationMethod.GPS_NETWORK);
            if (mPositioningManager != null) {
                mPositioningManager.addPositionListener(mPositionListener);
            }

        }

        isInitOK = true;
    }

    private void showEnableGPSDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getText(R.string.gps_not_enabled));

        builder.setPositiveButton(
                getResources().getText(R.string.action_settings),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                    }
                }
        );

        builder.setNegativeButton(
                getResources().getText(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );

        builder.create().show();
    }

    private final PositionListener mPositionListener = new PositionListener() {
        @Override
        public void onPositionUpdated(LocationMethod locationMethod, GeoPosition geoPosition) {

            if (isInitOK && mPositioningManager.hasValidPosition()) {


            }

        }

        @Override
        public void onPositionFixChanged(LocationMethod locationMethod, LocationStatus locationStatus) {

        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            mRootView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);

            String query;

            if (intent.hasExtra(SearchManager.QUERY)) {

                query = intent.getStringExtra(SearchManager.QUERY);

                requestUrl = RequestFactory.getGeolookupUrlString(this, query);
                //Log.i(TAG, "intent has extras: "+intent.getStringExtra(SearchManager.QUERY));
            } else {

                query = intent.getDataString();

                //Log.i(TAG, "intent has data string: "+query);

                requestUrl = RequestFactory.getGeolookupUrlString(this, query);
            }

            load(false);

        }
    }

    private void load(boolean forceload) {

        if (forceload) {

            Loader<ArrayList<Location>> loader = this.getLoaderManager().getLoader(LOADER_ID);
            if (loader != null) {
                loader.forceLoad();
            }

        } else {
            getLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);
        }

    }

    private static String formatGeoCoordinate(String value) {

        DecimalFormat format = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.US));

        return format.format(Double.parseDouble(value));

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isInitOK && mPositioningManager != null) {
            //Log.d(TAG, "Starting position manager");

            mPositioningManager.start(LocationMethod.GPS_NETWORK);
            if (mPositionListener != null) {
                mPositioningManager.addPositionListener(mPositionListener);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Log.i(TAG, "[onOptionsItemSelected]");

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();


        getLoaderManager().destroyLoader(LOADER_ID);

        if (isInitOK && mPositioningManager != null) {
            //Log.d(TAG, "Stopping position manager");
            mPositioningManager.stop();

            mPositioningManager.removePositionListener(mPositionListener);

        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private final LoaderManager.LoaderCallbacks<ArrayList<SearchLocation>> loaderCallbacks = new LoaderManager.LoaderCallbacks<ArrayList<SearchLocation>>() {

        @Override
        public Loader<ArrayList<SearchLocation>> onCreateLoader(int i, Bundle bundle) {
            //Log.i(TAG, "onCreateLoader");

            return new LocationLoader(SearchLocationActivity.this, requestUrl);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<SearchLocation>> arrayListLoader, ArrayList<SearchLocation> locations) {
            //Log.i(TAG, "[onLoadFinished] " + locations.size());

            if (locations.size() > 0) {
                mListAdapter.clear();

                for (SearchLocation l : locations) {

                    mListAdapter.add(l);

                    if (l.getLat() != null) addMapMarker(l);

                }

                SearchLocation location = locations.get(0);

                if (isInitOK && location.getLat() != null) {
                    mMap.setCenter(MapFactory.createGeoCoordinate(Double.parseDouble(location.getLat()),
                            Double.parseDouble(location.getLon())), MapAnimation.NONE);
                }else{
                    mMapFrame.setVisibility(View.GONE);
                }

                mProgressBar.setVisibility(View.GONE);
                mRootView.setVisibility(View.VISIBLE);
            }

        }

        private void addMapMarker(SearchLocation mStation) {


            try {

                if (isInitOK) {
                    MapMarker mapMarker;
                    GeoCoordinate mGeoCoordinate = MapFactory.createGeoCoordinate(Double.parseDouble(mStation.getLat()),
                            Double.parseDouble(mStation.getLon()));

                    mapMarker = MapFactory.createMapMarker(mGeoCoordinate, createMarkerImage());
                    mapMarker.setTitle(getResources().getString(R.string.station_title) + " " + mStation.getCity() + " " + mStation.getCountry());
                    mapMarker.setDescription(formatGeoCoordinate(mStation.getLat()) + ", " + formatGeoCoordinate(mStation.getLon())
                            + "\nID:" + mStation.getL());
                    mMap.addMapObject(mapMarker);

                    mapMarkerList.put(mapMarker.hashCode(), mapMarker);

                    mapStation.put(mapMarker.hashCode(), mStation);
                }


            } catch (IOException e) {

            }

        }


        private Image createMarkerImage() throws IOException {

            Image myImage = MapFactory.createImage();

            myImage.setImageResource(R.drawable.ic_location);

            return myImage;
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<SearchLocation>> arrayListLoader) {

        }
    };



    public static class SearchStationListAdapter extends ArrayAdapter<SearchLocation> {

        private final ArrayList<SearchLocation> locations;
        private final Context context;
        private final int layoutId;


        public SearchStationListAdapter(Context context, ArrayList<SearchLocation> objects) {
            super(context, R.layout.search_location_list_row, objects);

            this.context = context;
            this.layoutId = R.layout.search_location_list_row;
            this.locations = objects;
        }

        class ViewHolder {

            private final TextView firstRow;
            private final TextView secondRow;

            public ViewHolder(View view) {

                firstRow = (TextView) view.findViewById(R.id.search_firstRow_TextView);
                secondRow = (TextView) view.findViewById(R.id.search_secondRow_TextView);
            }

            private void populateLocation(SearchLocation location) {

                String firstRowString = location.getCity() + ", " + location.getCountry();
                String secondRowString = "";

                if (location != null && (!location.getL().equals(""))) {
                    secondRowString = location.getL();
                }
                firstRow.setText(firstRowString.toUpperCase());
                secondRow.setText(secondRowString.toLowerCase());

            }
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return position >= 0 && position <= locations.size();
        }

        @Override
        public int getCount() {
            return locations.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = layoutInflater.inflate(layoutId, null);

                viewHolder = new ViewHolder(convertView);
                if (convertView != null) {
                    convertView.setTag(viewHolder);
                }
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.populateLocation(locations.get(position));

            return convertView;
        }

    }


}
