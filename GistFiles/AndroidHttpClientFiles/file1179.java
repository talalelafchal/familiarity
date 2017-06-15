package com.walter.pointend;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class DestinationActivity extends AppCompatActivity implements TextWatcher, LocationListener {// implements LocationListener

    MenuItem sendItem;
    @BindView(R.id.inputCode)
    MaterialEditText mMaterialEditTextCode;
    @BindView(R.id.inputBusinessName)
    MaterialEditText mMaterialEditTextBussinesName;
    @BindView(R.id.inputBusinesPhone)
    MaterialEditText mMaterialEditTextBusinesNumber;
    @BindView(R.id.inputPhysicalLocation)
    MaterialEditText mMaterialEditTextPhysicalLoc;
    @BindView(R.id.inputRegion)
    MaterialEditText mMaterialEditTextRegigion;
    @BindView(R.id.inputTown)
    MaterialEditText mMaterialEditTextTown;
    @BindView(R.id.inputLandMark)
    MaterialEditText mMaterialEditTextLandmark;

    boolean notArrived = true;


    String names = "";
    String phone = "";

    LocationManager locationManager;
    //ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_location);
        ButterKnife.bind(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        names = prefs.getString("names", "John Doe");
        phone = prefs.getString("phone", "");
        //tvNames.setText(names);
/*        dialog = new ProgressDialog(DestinationActivity.this,R.style.full_screen_dialog){
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.full_screen_dialog);
                getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            }
        };
        dialog.setCancelable(false);*/

        mMaterialEditTextTown.addTextChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        sendItem = menu.findItem(R.id.send);
        sendItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO send items
        if (item.getItemId() == R.id.send) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        String code = mMaterialEditTextCode.getText().toString().trim();
        String bizName = mMaterialEditTextBussinesName.getText().toString().trim();
        String bizPhoneNumber = mMaterialEditTextBusinesNumber.getText().toString().trim();
        String physicalLocation = mMaterialEditTextPhysicalLoc.getText().toString().trim();
        String region = mMaterialEditTextRegigion.getText().toString().trim();
        String town = mMaterialEditTextTown.getText().toString().trim();

        if (!code.isEmpty() && !bizName.isEmpty() && !bizPhoneNumber.isEmpty() && !physicalLocation.isEmpty() && !region.isEmpty() && !town.isEmpty()) {
            sendItem.setVisible(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        String code = mMaterialEditTextCode.getText().toString().trim();
        String bizName = mMaterialEditTextBussinesName.getText().toString().trim();
        String bizPhoneNumber = mMaterialEditTextBusinesNumber.getText().toString().trim();
        String physicalLocation = mMaterialEditTextPhysicalLoc.getText().toString().trim();
        String region = mMaterialEditTextRegigion.getText().toString().trim();
        String town = mMaterialEditTextTown.getText().toString().trim();
        if (!code.isEmpty() && !bizName.isEmpty() && !bizPhoneNumber.isEmpty() && !physicalLocation.isEmpty() && !region.isEmpty() && !town.isEmpty()) {
            //sendItem.setVisible(true);
        }
    }

    public void update_location() {
        Log.d("POINTY", "Started GPS WORK");
        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGPSEnabled) {
            showSettingsAlert();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},101);
                }
                else
                {
                    Log.d("POINTY","Permission Granted");
                }
                return;
            }
            //progress.setVisibility(View.VISIBLE);
            //dialog.show();

            int attempts=0;
            while (notArrived) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                attempts++;
                if (attempts<=23) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    Log.d("POINTY", "GPS Enabled");
                    if (locationManager != null) {
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            //progress.setVisibility(View.VISIBLE);
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            notArrived = false;
                            Log.d("STATE_GPS", latitude + " " + longitude);
                            Log.d("POINTY",latitude + " " + longitude);

                            getMyLocationAddress(latitude, longitude);
                            //progress.setVisibility(View.INVISIBLE);
                        } else{
                            //Toast.makeText(this, "GPS is Still Getting Cordinates", Toast.LENGTH_SHORT).show();
                            Log.d("STATE_GPS", "Trying To Fetch Cordinates");
                            Log.d("POINTY","Trying To Fetch Cordinates");
                        }
                    }
                }else{
                    //dialog.dismiss();
                    stopUsingGPS();
                    break;
                }
            }


        }
    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS SETTINGS");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. You need to enable it in the settings?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        //tvGPStatus.setText("GPS is Enabled");
        Log.d("GPS_STATUS", "Provider enabled");
        Log.d("POINTY","GPS DEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        //tvGPStatus.setText("GPS is currently disabled");
        Log.d("GPS_STATUS", "Provider disabled");
        Log.d("POINTY","GPS Disabled");
    }

    public void stopUsingGPS() {
        Log.d("POINTY","Stoped Using GPS");
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    public void getMyLocationAddress(double lati, double longi) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.INTERNET}, 102);
            }
        }


        //Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Log.d("POINTY","Started Geo Reversing");
        //progress.setVisibility(View.VISIBLE);
        try {

            //Place your latitude and longitude
   /*         List<Address> addresses = geocoder.getFromLocation(lati, longi, 1);


            if (addresses != null) {

                Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();

                for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
                }
*/
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                String time = dateFormat.format(date);
                String address = mMaterialEditTextPhysicalLoc.getText().toString().trim();//strAddress.toString();
                double longitude = longi;
                double latitude = lati;
                String phoneNumber = phone;
                //String address, String longitude, String latitude, String time, String date, String code, String bizName, String bizTelPhone, String physicalLocation, String region, String town, String landmark) {
                String code = mMaterialEditTextCode.getText().toString().trim();
                String bizName = mMaterialEditTextBussinesName.getText().toString().trim();
                String bizPhoneNumber = mMaterialEditTextBusinesNumber.getText().toString().trim();
                String physicalLocation = mMaterialEditTextPhysicalLoc.getText().toString().trim();
                String region = mMaterialEditTextRegigion.getText().toString().trim();
                String town = mMaterialEditTextTown.getText().toString().trim();
                upload_location(new Item(address, longitude + "", latitude + "", time, "", code, bizName, bizPhoneNumber, physicalLocation, region, town, mMaterialEditTextLandmark.getText().toString()));
                //tvLocation.setText("I am at: " + strAddress.toString());
                //progress.setVisibility(View.INVISIBLE);

            /*} else {
                //tvLocation.setText("No location found..!");
                //progress.setVisibility(View.INVISIBLE);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not get address..!", Toast.LENGTH_LONG).show();
        }

    }

    private void upload_location(Item item) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        Log.d("POINTY","Started Uploading Info");
        params.put("phone", phone);
        params.put("address", item.getAddress());
        params.put("longitude", item.getLongitude());
        params.put("latitude", item.getLatitude());
        params.put("time", item.getTime());

        params.put("code", item.getCode());
        params.put("biz_name", item.getBizName());
        params.put("physical_location", item.getPhysicalLocation());
        params.put("biz_tel_num", item.getBizTelPhone());
        params.put("biz_region", item.getRegion());
        params.put("biz_town", item.getTown());
        params.put("biz_landmark", item.getLandmark());
        client.post(Constants.URL_LOCATION_ADD, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("SERVER", responseString);
                //dialog.dismiss();
                stopUsingGPS();
                Toast.makeText(getApplicationContext(), "Failed To Upload Location Details To The Server. Try Again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("SERVER", responseString);
                //dialog.dismiss();
                stopUsingGPS();
                try {
                    JSONObject obj = new JSONObject(responseString);
                    String resp = obj.getString("response");
                    if(resp.toLowerCase().contains("succes")){
                        clear_fields();
                        finish();
                        startActivity(new Intent(DestinationActivity.this, AdminActivity.class));
                    }

                    Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error while processing response from server", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void clear_fields() {
        mMaterialEditTextBussinesName.setText("");
        mMaterialEditTextBusinesNumber.setText("");
        mMaterialEditTextTown.setText("");
        mMaterialEditTextPhysicalLoc.setText("");
        mMaterialEditTextCode.setText("");
        mMaterialEditTextLandmark.setText("");
        mMaterialEditTextRegigion.setText("");
        mMaterialEditTextCode.requestFocus();
    }

    public void register(View view) {
        Log.d("POINTY","Started Submitting");
        String code = mMaterialEditTextCode.getText().toString().trim();
        String bizName = mMaterialEditTextBussinesName.getText().toString().trim();
        String bizPhoneNumber = mMaterialEditTextBusinesNumber.getText().toString().trim();
        String physicalLocation = mMaterialEditTextPhysicalLoc.getText().toString().trim();
        String region = mMaterialEditTextRegigion.getText().toString().trim();
        String town = mMaterialEditTextTown.getText().toString().trim();
        if (!code.isEmpty() && !bizName.isEmpty() && !bizPhoneNumber.isEmpty() && !physicalLocation.isEmpty() && !region.isEmpty() && !town.isEmpty()) {
            //sendItem.setVisible(true);
            update_location();

        }else{
            Toast.makeText(this, "You must provide all the required info", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("POINT", "GPS PERMISSION WAS FINALLY GRANTED");
                }
                break;
            case  102:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("POINT", "INTERNET PERMISSION WAS FINALLY GRANTED");
                    update_location();
                }
               break;
        }
    }
}
