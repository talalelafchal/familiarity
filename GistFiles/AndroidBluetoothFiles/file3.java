package com.dt.cstag;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.Keep;
import android.support.v4.content.ContextCompat;

/**
 * Checks requirements, that are needed to run a btle operation.
 */
@Keep
public class CsTagRequirementChecker {
    private Context context;
    private boolean isWriteStorageRequired = true;

    public CsTagRequirementChecker setWriteStorageRequired (boolean writeStorageRequired) {
        isWriteStorageRequired = writeStorageRequired;
        return this;
    }

    public CsTagRequirementChecker(Context context) {
        this.context = context;
    }

    public boolean checkSilently () {
        return checkLeSupported()
                && checkLocationPermission()
                && checkBluetoothIsOn()
                && checkLocationIsOn()
                && (isWriteStorageRequired ? checkWritePermission() : true);
    }

    private boolean checkWritePermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isReadExternalStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
            return isReadExternalStorage;
        } else {
            return true;
        }
    }

    public void check(RequirementListener listener) {
        if(listener == null)
            return;
        if (!checkLeSupported()) {
            listener.onRequirementsError(FailureType.BLUETOOTH_LE_NOT_SUPPORTED);
        } else if (!checkLocationPermission()) {
            listener.onRequirementsError(FailureType.NO_LOCATION_PERMISSION);
        } else if (!checkBluetoothIsOn()) {
            listener.onRequirementsError(FailureType.BLUETOOTH_IS_OFF);
        } else if (!checkLocationIsOn()) {
            listener.onRequirementsError(FailureType.LOCATION_IS_OFF);
        } else if (!checkWritePermission() && isWriteStorageRequired) {
            listener.onRequirementsError(FailureType.WRITE_STORAGE_PERMISSION_IS_OFF);
        }
    }

    private boolean checkLeSupported() {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    private boolean checkLocationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
            boolean isCourseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
            return isCourseLocation || isFineLocation;
        } else {
            return true;
        }
    }

    private boolean checkBluetoothIsOn() {
        BluetoothManager bluetoothManager = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE));
        if (bluetoothManager == null) return false;
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) return false;
        if (!bluetoothAdapter.isEnabled()) return false;
        return true;
    }

    private boolean checkLocationIsOn() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = false;
        boolean isNetworkEnabled = false;
        try {
            isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isGpsEnabled || isNetworkEnabled;
    }

    @Keep
    public enum FailureType {
        BLUETOOTH_LE_NOT_SUPPORTED,
        NO_LOCATION_PERMISSION,
        BLUETOOTH_IS_OFF,
        LOCATION_IS_OFF,
        WRITE_STORAGE_PERMISSION_IS_OFF
    }

    @Keep
    public interface RequirementListener {
        void onRequirementsError(FailureType type);
    }
}
