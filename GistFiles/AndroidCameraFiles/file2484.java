package com.travelr.example.markmyplaces.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.travelr.example.markmyplaces.R;
import com.travelr.example.markmyplaces.app.PlacesApplication;
import com.travelr.example.markmyplaces.db.MyPlace;
import com.travelr.example.markmyplaces.services.GeoCoderIntentService;
import com.travelr.example.markmyplaces.services.GeoFenceIntentService;

public class AddPlaceActivity extends MyPlaceBaseActivity {

    public static final String EXTRA_LAT_LNG = "extra_lat_lng";
    public static final String EXTRA_PLACE_ID = "place_id";
    public static final String EXTRA_VIEW_TYPE = "view_type";
    private static final int TYPE_ADD = 501;
    private static final int TYPE_UPDATE = 502;
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = Geofence.NEVER_EXPIRE;
    private SupportMapFragment mMapFragment = null;
    private GoogleMap mGoogleMap = null;
    private Location mCurrentLocation = null;
    private AddreddResultReceiver mAddressReceiver = null;
    private EditText mEdtTitle = null;
    private EditText mEdtMessage = null;
    private TextView mTxtAddress = null;
    private Button mBtnAddPlacce = null;
    private CheckBox mChkAddToGeoFence = null;
    private EditText mEdtRadius = null;
    private boolean isRequestingAddress = false;
    private boolean isWaitingForGeocodeToCommit = false;
    private int mGeoCodeResult = GeoCoderIntentService.RESULT_FAILURE;
    private String mGeoCodedAddress = null;
    private Toolbar mToolbar = null;
    private int mCurrentViewType = TYPE_ADD;
    private long mPlaceId = -1;
    private MyPlace mPlaceToUpdate = null; // init only when viewType ==
                                           // TYPE_UPDATE
    private List<MyPlace> mListGeofences = null;
    private PendingIntent mGeofencePendingIntent = null;
    private GoogleApiClient mGoogleApiClient = null;
    private boolean isGoogleApiConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataFromIntent();
        checkDataAndInflate();
    }

    private void initComponents() {
        mAddressReceiver = new AddreddResultReceiver(new Handler());
        mListGeofences = PlacesApplication.getDatabase(getBaseContext()).getGeoFencedPlaces();
        mPlaceToUpdate = (mCurrentViewType == TYPE_UPDATE) ? PlacesApplication.getDatabase(getBaseContext()).getPlace(mPlaceId) : null;
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(mGoogleApiCallback)
                .addOnConnectionFailedListener(mConnectionFailedCallback).addApi(LocationServices.API).build();
    }

    private void onGoogleApiEnabled(Bundle connectionHint) {
        isGoogleApiConnected = true;
    }

    private void onGoogleApiDisabled() {
        isGoogleApiConnected = false;
    }

    private OnConnectionFailedListener mConnectionFailedCallback = new OnConnectionFailedListener() {

        @Override
        public void onConnectionFailed(ConnectionResult arg0) {
            onGoogleApiDisabled();
        }
    };
    private ConnectionCallbacks mGoogleApiCallback = new ConnectionCallbacks() {

        @Override
        public void onConnectionSuspended(int arg0) {
            onGoogleApiDisabled();

        }

        @Override
        public void onConnected(Bundle connectionHint) {
            onGoogleApiEnabled(connectionHint);

        }
    };

    private void checkDataAndInflate() {
        if (isValidData()) {
            initComponents();
            setContentView(R.layout.activity_mark_place);
            initViews();
            setUpView();
            checkAndUpdateLocation();
        } else {
            PlacesApplication.showGenericToast(getBaseContext(), getString(R.string.failure));
            finish();
        }

    }

    private void checkAndUpdateLocation() {
        if (mCurrentViewType == TYPE_ADD) {
            requestAddressForLocation();
        }

    }

    private void setUpView() {
        if (mCurrentViewType == TYPE_ADD) {
            disableGeoFenceUI();
        } else {
            updateViewWithPlace(mPlaceToUpdate);
        }
    }

    private void updateViewWithPlace(MyPlace myPlace) {
        mEdtMessage.setText(myPlace.getMessage());
        mEdtTitle.setText(myPlace.getTitle());
        mTxtAddress.setText(myPlace.getAddress());
        if (myPlace.isAddedToFence()) {
            enableGeoFenceUI();
        } else {
            disableGeoFenceUI();
        }
        mEdtRadius.setText("" + myPlace.getRadius());
        mBtnAddPlacce.setText(getString(R.string.update_place));
    }

    private void requestAddressForLocation() {
        isRequestingAddress = true;
        GeoCoderIntentService.launchGeocodeService(getBaseContext(), mAddressReceiver, mCurrentLocation);

    }

    private void setupLiteMap() {
        mMapFragment = SupportMapFragment.newInstance(getMapOptionsLite());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.flMapContainer, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(mOnMapReadyCallBack);

    }

    private void setupGoogleMapLite(GoogleMap googleMap, LatLng placeLatLng) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(false);
        mGoogleMap.addMarker(new MarkerOptions().position(placeLatLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 18.0f));
    }

    private GoogleMapOptions getMapOptionsLite() {
        GoogleMapOptions mMapOptions = new GoogleMapOptions();
        mMapOptions.compassEnabled(false);
        mMapOptions.mapToolbarEnabled(false);
        mMapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL);
        mMapOptions.rotateGesturesEnabled(false);
        mMapOptions.zoomControlsEnabled(false);
        mMapOptions.liteMode(true);
        return mMapOptions;
    }

    private LatLng getCurrentLocation() {
        return (mCurrentViewType == TYPE_ADD) ? new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()) : new LatLng(
                mPlaceToUpdate.getLatitude(), mPlaceToUpdate.getLongitude());
    }

    private OnMapReadyCallback mOnMapReadyCallBack = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            setupGoogleMapLite(googleMap, getCurrentLocation());
        }

    };

    private ResultCallback<Status> mGeofenceResultCallback = new ResultCallback<Status>() {

        @Override
        public void onResult(Status arg0) {

        }
    };

    private boolean isValidData() {
        boolean result = false;
        if (mCurrentViewType == TYPE_ADD) {
            result = mCurrentLocation != null;
        } else {
            result = mPlaceId > 0;
        }
        return result;
    }

    private void initViews() {
        mEdtMessage = (EditText) findViewById(R.id.edtMessage);
        mEdtTitle = (EditText) findViewById(R.id.edtTitle);
        mEdtRadius = (EditText) findViewById(R.id.edtRadius);
        mChkAddToGeoFence = (CheckBox) findViewById(R.id.chkAddToGeofence);
        mTxtAddress = (TextView) findViewById(R.id.txtAddress);
        mBtnAddPlacce = (Button) findViewById(R.id.btnDone);
        mBtnAddPlacce.setOnClickListener(mOnClickListener);
        mEdtMessage.setOnEditorActionListener(mOnEditorActionListener);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mChkAddToGeoFence.setOnClickListener(mOnClickListener);
        setupLiteMap();
        initActionBar();
        disableGeoFenceUI();
    }

    private void initActionBar() {
        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setUpToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    private void updateAddressUI(int resultCode, Bundle resultData) {
        mGeoCodedAddress = resultData.getString(GeoCoderIntentService.EXTRA_RESULT_DATA);
        mTxtAddress.setText(mGeoCodedAddress);

    }

    private void initDataFromIntent() {
        mCurrentLocation = getIntent().getParcelableExtra(EXTRA_LAT_LNG);
        mCurrentViewType = getIntent().getIntExtra(EXTRA_VIEW_TYPE, TYPE_ADD);
        mPlaceId = getIntent().getLongExtra(EXTRA_PLACE_ID, -1);
        buildGoogleApiClient();

    }

    private boolean isTitleValid() {
        String title = mEdtTitle.getText().toString();
        return isValidString(title);
    }

    private boolean isMessageValid() {
        String title = mEdtMessage.getText().toString();
        return isValidString(title);
    }

    private boolean isValidString(String title) {
        return title != null && !title.trim().isEmpty();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onClickAddPlace() {
        if (isTitleValid()) {
            if (isMessageValid()) {
                addOrUpdatePlaceData();

            } else {
                mEdtMessage.setError(getString(R.string.error_enter_data));
            }
        } else {
            mEdtTitle.setError(getString(R.string.error_enter_data));

        }

    }

    private void addOrUpdatePlaceData() {
        if (mCurrentViewType == TYPE_UPDATE) {
            MyPlace placeToUpdate = getUpdatedPlace();
            updatePlaceAndFinish(placeToUpdate);
        } else {
            checkAddressLoadedAndCommit(mCurrentLocation, mEdtTitle.getText().toString().trim(), mEdtMessage.getText().toString().trim());
        }
    }

    private void updatePlaceAndFinish(MyPlace placeToUpdate) {
        if (PlacesApplication.getDatabase(getBaseContext()).updateMyPlace(placeToUpdate.getDbId(), placeToUpdate)) {
            checkAndAddPlaceToGeoFence(placeToUpdate);
        } else {
            PlacesApplication.showGenericToast(getBaseContext(), getString(R.string.failure));
        }
        finish();
    }

    private MyPlace getUpdatedPlace() {
        mPlaceToUpdate.setAddress(mTxtAddress.getText().toString().trim());
        mPlaceToUpdate.setMessage(mEdtMessage.getText().toString().trim());
        mPlaceToUpdate.setTitle(mEdtTitle.getText().toString().trim());
        mPlaceToUpdate.setIsAddedToFence(mChkAddToGeoFence.isChecked());
        if(!mPlaceToUpdate.isAddedToFence()){
            mPlaceToUpdate.setRadius(MyPlace.RADIUS_DEFAULT);
        }
        mPlaceToUpdate.setRadius(Integer.parseInt(mEdtRadius.getText().toString().trim()));
        return mPlaceToUpdate;
    }

    private void onClickCheckAddToGeofence() {
        if (mChkAddToGeoFence.isChecked()) {
            if (isValidForFencing()) {
                enableGeoFenceUI();
            } else {
                disableGeoFenceUI();
                PlacesApplication.showGenericToast(getBaseContext(), getString(R.string.geofencing_limit_exceeded));
            }
        } else {
            disableGeoFenceUI();
        }
    }

    private void enableGeoFenceUI() {
        mChkAddToGeoFence.setChecked(true);
        mEdtRadius.setEnabled(true);
    }

    private void disableGeoFenceUI() {
        mChkAddToGeoFence.setChecked(false);
        mEdtRadius.setEnabled(false);
    }

    private boolean isValidForFencing() {
        return mListGeofences.size() < PlacesApplication.MAX_GEOFENCES || mListGeofences.contains(mPlaceToUpdate);
    }

    private void checkAddressLoadedAndCommit(Location curreLocation, String title, String message) {
        if (!isRequestingAddress) {
            addNewPlaceAndFinish(curreLocation, title, message);
        } else {
            isWaitingForGeocodeToCommit = true;
            showProgressDialog(getString(R.string.loading_address), false);
        }

    }

    private void addNewPlaceAndFinish(Location curreLocation, String title, String message) {

        MyPlace newMyPlace = getData(curreLocation, title, message);
        long result = PlacesApplication.getDatabase(getBaseContext()).insertMyPlace(newMyPlace);
        if (result > 0) {
            PlacesApplication.showGenericToast(getBaseContext(), getString(R.string.place_added));
            if(newMyPlace.isAddedToFence()){
                newMyPlace.setDbId(result);
                checkAndAddPlaceToGeoFence(newMyPlace);
            }
        } else {
            PlacesApplication.showGenericToast(getBaseContext(), getString(R.string.failure));
        }
        finish();
    }

    private void checkAndAddPlaceToGeoFence(MyPlace newMyPlace) {
        if (isGoogleApiConnected) {
            addOrRemoveGeofence(newMyPlace);
        } else {
            PlacesApplication.showGenericToast(getBaseContext(), getString(R.string.failure));
        }
    }

    private void addOrRemoveGeofence(MyPlace place) {
        if (place.isAddedToFence()) {
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(place), getGeoFencePendingIntent(place))
                    .setResultCallback(mGeofenceResultCallback);
        }else{
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, getGeoFencePendingIntent(place));
        }

    }

    private MyPlace getData(Location curreLocation, String title, String message) {
        String address = getAddress();
        return new MyPlace(address, title, message, curreLocation.getLatitude(), curreLocation.getLongitude(), mChkAddToGeoFence.isChecked(),
                System.currentTimeMillis(), Integer.valueOf(mEdtRadius.getText().toString()));
    }

    private String getAddress() {
        return mGeoCodedAddress;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    private GeofencingRequest getGeofencingRequest(MyPlace place) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT );
        builder.addGeofence(getGeoFenceForPlace(place));
        return builder.build();
    }

    private Geofence getGeoFenceForPlace(MyPlace place) {
        return new Geofence.Builder().setRequestId("" + place.getDbId())
                .setCircularRegion(place.getLatitude(), place.getLongitude(), place.getRadius())
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT).build();
    }

    private PendingIntent getGeoFencePendingIntent(MyPlace place) {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeoFenceIntentService.class);
        intent.putExtra(EXTRA_PLACE_ID, place.getDbId());
        return PendingIntent.getService(this, (int) place.getDbId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void checkForDataAfterGeocode() {
        if (isWaitingForGeocodeToCommit) {
            isWaitingForGeocodeToCommit = false;
            dismissProgressDialog();
            addNewPlaceAndFinish(mCurrentLocation, mEdtTitle.getText().toString().trim(), mEdtMessage.getText().toString().trim());
        }

    }

    private OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (v.getId()) {
                case R.id.edtMessage:
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        onClickAddPlace();
                    }
                    break;

                default:
                    break;
            }
            return false;
        }
    };
    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View clickedView) {
            switch (clickedView.getId()) {
                case R.id.btnDone:
                    onClickAddPlace();
                    break;
                case R.id.chkAddToGeofence:
                    onClickCheckAddToGeofence();
                    break;
                default:
                    break;
            }
        }

    };

    private class AddreddResultReceiver extends ResultReceiver {

        public AddreddResultReceiver(Handler handler) {
            super(handler);

        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            isRequestingAddress = false;
            mGeoCodeResult = resultCode;
            updateAddressUI(resultCode, resultData);
            checkForDataAfterGeocode();
            super.onReceiveResult(resultCode, resultData);
        }

    }

    public static void launchAddPlace(Context context, Location mCurrentLocation) {
        Intent addPlaceIntent = new Intent(context, AddPlaceActivity.class);
        addPlaceIntent.putExtra(AddPlaceActivity.EXTRA_LAT_LNG, mCurrentLocation);
        context.startActivity(addPlaceIntent);
    }

    public static void launchAddPlace(Context context, MyPlace placeToUpdate) {
        Intent addPlaceIntent = new Intent(context, AddPlaceActivity.class);
        addPlaceIntent.putExtra(AddPlaceActivity.EXTRA_VIEW_TYPE, AddPlaceActivity.TYPE_UPDATE);
        addPlaceIntent.putExtra(AddPlaceActivity.EXTRA_PLACE_ID, placeToUpdate.getDbId());
        context.startActivity(addPlaceIntent);
    }
}
