package com.example.roman.echoparkrecorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import com.apkfuns.logutils.LogUtils;

public class PermissionsActivity extends AppCompatActivity {

    private Switch mLocationSwitch;
    private Switch mStorageSwitch;
    private Switch mAudioSwitch;
    private Switch mCameraSwitch;

    private static final int LOCATION_PERMISSION_CODE = 000;
    private static final int STORAGE_PERMISSION_CODE = 111;
    private static final int AUDIO_PERMISSION_CODE = 222;
    private static final int CAMERA_PERMISSION_CODE = 333;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        if(checkAllPermissionsGranted()){
            goToNextActivity();
        }

        mLocationSwitch = (Switch) findViewById(R.id.location_switch);
        mStorageSwitch = (Switch) findViewById(R.id.storage_switch);
        mAudioSwitch = (Switch) findViewById(R.id.audio_switch);
        mCameraSwitch =(Switch) findViewById(R.id.camera_switch);

        //set switches on and unclickable for switches with already granted permissions
        //MUST BE CALLED BEFORE ADD SWITCH STATE CHANGE LISTENER
        setSwitchStates();

        addSwitchStateChangeListeners();


    }


    /**
     * set switches on and unclickable for switches with already granted permissions
     */
    private void setSwitchStates(){

        if(checkLocationPermission()){
            mLocationSwitch.setChecked(true);
            mLocationSwitch.setClickable(false);
        }

        if(checkStoragePermission()){
            mStorageSwitch.setChecked(true);
            mStorageSwitch.setClickable(false);
        }

        if(checkAudioPermission()){
            mAudioSwitch.setChecked(true);
            mAudioSwitch.setClickable(false);
        }

        if(checkCameraPermission()){
            mCameraSwitch.setChecked(true);
            mCameraSwitch.setClickable(false);
        }
    }


    /**
     *
     * @return true if ACCESS_FINE_LOCATION already granted, false otherwise
     */
    public boolean checkLocationPermission(){
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    /**
     *
     * @return true if WRITE_EXTERNAL_STORAGE already granted, false otherwise
     */
    public boolean checkStoragePermission(){
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    /**
     *
     * @return true if RECORD_AUDIO already granted, false otherwise
     */
    public boolean checkAudioPermission(){
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }
    /**
     *
     * @return true if CAMERA already granted, false otherwise
     */
    public boolean checkCameraPermission(){
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,},
                LOCATION_PERMISSION_CODE);
    }

    public void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                STORAGE_PERMISSION_CODE);

    }

    public void requestAudioPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.RECORD_AUDIO,},
                AUDIO_PERMISSION_CODE);
    }

    public void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA,},
                CAMERA_PERMISSION_CODE);

    }

    private void locationPermissionResponse(boolean permissionState){
        if(permissionState){
            mLocationSwitch.setChecked(true);
            mLocationSwitch.setClickable(false);
            if(checkAllPermissionsGranted()){
                goToNextActivity();
            }
            return;
        }

        mLocationSwitch.setChecked(false);
        Toast.makeText(PermissionsActivity.this, "THE APP NEEDS LOCATION PERMISSION TO WORK", Toast.LENGTH_SHORT).show();
    }

    private void storagePermissionResponse(boolean permissionState){
        if(permissionState){
            mStorageSwitch.setChecked(true);
            mStorageSwitch.setClickable(false);

            if(checkAllPermissionsGranted()){
                goToNextActivity();
            }
            return;
        }

        mStorageSwitch.setChecked(false);
        Toast.makeText(PermissionsActivity.this, "THE APP NEEDS STORAGE PERMISSION TO WORK", Toast.LENGTH_SHORT).show();
    }

    private void audioPermissionResponse(boolean permissionState){
        if(permissionState){
            mAudioSwitch.setChecked(true);
            mAudioSwitch.setClickable(false);
            if(checkAllPermissionsGranted()){
                goToNextActivity();
            }
            return;
        }

        mAudioSwitch.setChecked(false);
        Toast.makeText(PermissionsActivity.this, "THE APP NEEDS AUDIO PERMISSION TO WORK", Toast.LENGTH_SHORT).show();    }

    private void cameraPermissionResponse(boolean permissionState){
        if(permissionState){
            mCameraSwitch.setChecked(true);
            mCameraSwitch.setClickable(false);

            if(checkAllPermissionsGranted()){
                goToNextActivity();
            }

            return;
        }

        mCameraSwitch.setChecked(false);
        Toast.makeText(PermissionsActivity.this, "THE APP NEEDS CAMERA PERMISSION TO WORK", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE: {
                LogUtils.d("case LOCATION_PERMISSION_CODE");
                if(checkRequestGranted(grantResults)){
                    // permission was granted, yay!
                    locationPermissionResponse(true);

                } else {
                    // permission denied, boo!
                    locationPermissionResponse(false);
                }
                return;
            }
            case STORAGE_PERMISSION_CODE: {
                LogUtils.d("case STORAGE_PERMISSION_CODE");
                if(checkRequestGranted(grantResults)){
                    // permission was granted, yay!
                    storagePermissionResponse(true);
                } else {
                    // permission denied, boo!
                    storagePermissionResponse(false);
                }
                return;
            }
            case AUDIO_PERMISSION_CODE: {
                LogUtils.d("case AUDIO_PERMISSION_CODE");
                if(checkRequestGranted(grantResults)){
                    // permission was granted, yay!
                    audioPermissionResponse(true);
                } else {
                    // permission denied, boo!
                    audioPermissionResponse(false);
                }
                return;
            }
            case CAMERA_PERMISSION_CODE: {
                LogUtils.d("case CAMERA_PERMISSION_CODE");
                if(checkRequestGranted(grantResults)){
                    // permission was granted, yay!
                    cameraPermissionResponse(true);
                } else {
                    // permission denied, boo!
                    cameraPermissionResponse(false);
                }
                return;
            }
            default: {
                LogUtils.d(" DEFAULT PERMISSION RESULT? WTF?");
            }
        }
    }


    /**
     * checks whether the permission was granted or not
     * @param grantResults results array
     * @return boolean granted or not
     */
    private boolean checkRequestGranted( @NonNull int[] grantResults){
        // If request is cancelled, the result arrays are empty.
        return grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkAllPermissionsGranted(){
        return checkLocationPermission()
                && checkStoragePermission()
                && checkAudioPermission()
                && checkCameraPermission();
    }

    private void goToNextActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void addSwitchStateChangeListeners(){

        // listener for swtich fling/click/etc
        mLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    requestLocationPermission();
                }
            }
        });

        // listener for swtich fling/click/etc
        mStorageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    requestStoragePermission();
                }
            }
        });

        // listener for swtich fling/click/etc
        mAudioSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    requestAudioPermission();
                }
            }
        });

        // listener for swtich fling/click/etc
        mCameraSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    requestCameraPermission();
                }
            }
        });
    }

}