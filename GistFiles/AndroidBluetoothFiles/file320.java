package ...

import ...

public class PermissionsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private static final int BLUETOOTH_SETTINGS_CODE = 4000;
    private static final int LOCATION_SETTINGS_CODE = 5000;
    public static final int PERMISSION_REQUEST_FINE_LOCATION = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        
        permissionCheck();
        
    }

    private void permissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isPermissionGranted = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (isPermissionGranted) {
                openLocationSettings();
            } else {
                requestFineLocationPermission();
            }
        } else {
            openLocationSettings();
        }
    }


    private void openLocationSettings() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
    }

    private void requestFineLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SETTINGS_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                openBluetoothSettings();
                startNearRadar();
            }
        } else if (requestCode == BLUETOOTH_SETTINGS_CODE) {
            //Nothing to do
            if (resultCode == Activity.RESULT_OK) {
                startNearRadar();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).setNeedBle(true);

        final PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        startNearRadar();
                        // The bluetooth permissions are strictly necessary for beacons, 
                        // but not for geofences
                        openBluetoothSettings();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(
                                    PermissionsActivity.this,
                                    LOCATION_SETTINGS_CODE);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        break;
                }
            }
        });

    }

    private void openBluetoothSettings() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_SETTINGS_CODE);
        } else {
            Log.d(TAG, "All permission availble");
        }

    }

    private void startNearRadar() {
        // You have all the right permissions to start the NearIT radar
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Handle connection suspended error
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Handle connection failed error
    }
}