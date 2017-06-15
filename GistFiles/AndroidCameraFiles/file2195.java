package com.jalepo.fotomap;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class CameraActivity extends FotoMapActivity {
    private Rect buttonRect = new Rect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        buildGoogleApiClient();
        googleApiClient.connect();

        SelectMap();

        final EditText cancelButton = (EditText) findViewById(R.id.text_cancel);
        cancelButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    cancelButton.getDrawingRect(buttonRect);
                    int xCoord = (int) event.getX(0);
                    int yCoord = (int) event.getY(0);
                    if (buttonRect.contains(xCoord, yCoord)) {
                        if(locationManager != null && locationListener != null) {
                            locationManager.removeUpdates(locationListener);
                        }
                        finish();
                        return true;
                    }
                }
                return false;

            }
        });
    }

    public void SelectMap() {
        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        mapdatabase = databaseHelper.getReadableDatabase();
        String[] fromColumns = { DatabaseHelper.COLUMN_NAME_MAPNAME};

        Cursor cursor = mapdatabase.query(DatabaseHelper.MAP_TABLE_NAME,
                fromColumns,
                DatabaseHelper.COLUMN_NAME_DOWNLOADED + "=?",
                new String[] { FALSE },
                null,
                null,
                DatabaseHelper.COLUMN_NAME_MAPNAME + " ASC");

        cursor.moveToFirst();

        final ArrayList<String> mapsArray = new ArrayList<>();
        int count = 0;
        while(count < cursor.getCount()) {
            mapsArray.add(cursor.getString(0));
            cursor.moveToNext();
            count++;
        }
        cursor.close();
        mapsArray.add(getString(R.string.add_new_map));

        ListView mapList = (ListView) findViewById(R.id.listView_selectmap);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mapsArray);
        mapList.setAdapter(listAdapter);
        mapList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mapName = mapsArray.get(position);

                if (mapName.equals(getString(R.string.add_new_map))) {
                    if(locationManager != null && locationListener != null) {
                        locationManager.removeUpdates(locationListener);
                    }
                    Intent intent = new Intent(getApplicationContext(), MapListActivity.class);
                    intent.putExtra(ADD_NEW_MAP, true);
                    startActivity(intent);
                    finish();
                } else {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
        finish();

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //For any touch event not handled by this activity, turn of location updates and finish the activity.
        // This will close the window if the user touches outside of the visible portion
        if(!super.onTouchEvent(event)) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if(locationManager != null && locationListener != null) {
                    locationManager.removeUpdates(locationListener);
                }
                finish();
                return true;
            }
        }
        return false;
    }
}
